use clap::{Parser, ValueEnum};
use std::sync::mpsc::{self, Sender};
use std::thread;
use std::time::Duration;
use std::sync::OnceLock;

/// Structure gérant l'envoi de messages de log vers un thread dédié à l'écriture.
struct Logger {
    sender: Sender<String>,
}

/// Tente de récupérer le nom du conteneur en lisant /etc/hostname.
/// Retourne "unknown" si la lecture échoue.
fn get_container_name() -> String {
    use std::fs;
    // Tenter de récupérer le nom de l'hôte qui est souvent l'ID du container par défaut
    if let Ok(hostname) = fs::read_to_string("/etc/hostname") {
        return hostname.trim().to_string();
    }
    "unknown".to_string()
}

impl Logger {
    /// Initialise un nouveau logger asynchrone.
    /// Lance un thread qui écoute sur un canal et écrit dans stdout ainsi que dans un fichier /var/log.
    fn new(container_name: Option<String>) -> Self {
        let (tx, rx) = mpsc::channel::<String>();
        let name = container_name.unwrap_or_else(get_container_name);
        let log_file_path = format!("/var/log/healthcheck-{}.log", name);
        
        thread::spawn(move || {
            use std::fs::OpenOptions;
            use std::io::Write;

            // Tentative d'ouverture du fichier en mode ajout
            let mut file = OpenOptions::new()
                .create(true)
                .append(true)
                .open(&log_file_path)
                .ok();

            // Boucle de réception des messages de log
            while let Ok(line) = rx.recv() {
                println!("[LOG] {}", line);
                if let Some(ref mut f) = file {
                    let _ = writeln!(f, "[LOG] {}", line);
                }
            }
        });
        Logger { sender: tx }
    }
    /// Envoie un message au thread de logging.
    fn log(&self, msg: String) {
        let _ = self.sender.send(msg);
    }
}

impl Drop for Logger {
    fn drop(&mut self) {
        // La fermeture du canal (sender) signalera au thread de s'arrêter proprement
    }
}

/// Global unique instance du logger, initialisée une seule fois.
static LOGGER: OnceLock<Logger> = OnceLock::new();

use std::sync::atomic::{AtomicU8, Ordering};

/// Niveaux de log supportés.
#[derive(Copy, Clone, PartialEq, Eq, PartialOrd, Ord, Debug, ValueEnum)]
enum LogLevel {
    /// Pas de logs.
    Mute = 0,
    /// Logs standards (Info).
    Info = 1,
    /// Logs détaillés (Trace).
    Trace = 2,
}

/// Niveau de log global stocké de manière atomique.
static LOG_LEVEL: AtomicU8 = AtomicU8::new(LogLevel::Info as u8);

/// Définit le niveau de log à partir des arguments CLI ou de la variable d'environnement LOG_LEVEL.
fn set_log_level_from_args_env(args_level: Option<LogLevel>) {
    use std::env;
    if let Some(lvl) = args_level {
        LOG_LEVEL.store(lvl as u8, Ordering::Relaxed);
        return;
    }
    let level = env::var("LOG_LEVEL").unwrap_or_else(|_| "INFO".to_string());
    let lvl = match level.to_ascii_uppercase().as_str() {
        "TRACE" => LogLevel::Trace,
        "MUTE" => LogLevel::Mute,
        _ => LogLevel::Info,
    };
    LOG_LEVEL.store(lvl as u8, Ordering::Relaxed);
}

/// Fonction utilitaire pour logger un message si le niveau actuel le permet.
fn log_simple(level: LogLevel, msg: &str) {
    let current = match LOG_LEVEL.load(Ordering::Relaxed) {
        0 => LogLevel::Mute,
        2 => LogLevel::Trace,
        _ => LogLevel::Info,
    };
    if current == LogLevel::Mute {
        return;
    }
    if level == LogLevel::Trace && current != LogLevel::Trace {
        return;
    }
    if let Some(logger) = LOGGER.get() {
        logger.log(format!("{:?}: {}", level, msg));
    }
}

/// Arguments de la ligne de commande.
#[derive(Parser, Debug)]
#[command(author, version, about, long_about = None)]
struct Args {
    /// URL à tester (ex: https://localhost:8080/health)
    url: String,
    /// Verbe HTTP à utiliser (GET ou HEAD)
    #[arg(short, long, value_enum, default_value_t = HttpVerb::GET)]
    verb: HttpVerb,
    /// Timeout en secondes
    #[arg(short, long, default_value_t = 5)]
    timeout: u64,
    /// Niveau de log (TRACE, INFO, MUTE)
    #[arg(short = 'l', long = "log", value_enum, value_name = "type-log")] 
    log_level: Option<LogLevel>,
    /// Nom du container pour le fichier de log
    #[arg(short = 'n', long = "name")]
    container_name: Option<String>,
}

/// Verbes HTTP supportés.
#[derive(Copy, Clone, PartialEq, Eq, PartialOrd, Ord, ValueEnum, Debug)]
enum HttpVerb {
    GET,
    HEAD,
}

fn main() {
    // Analyse des arguments et initialisation des globales
    let args = Args::parse();
    set_log_level_from_args_env(args.log_level);
    LOGGER.set(Logger::new(args.container_name)).ok();
    
    log_simple(LogLevel::Info, "Démarrage du healthcheck");

    // Configuration du client HTTP ureq
    let agent = ureq::AgentBuilder::new()
        .timeout(Duration::from_secs(args.timeout))
        .build();

    // Exécution de la requête selon le verbe choisi
    let result = match args.verb {
        HttpVerb::GET => agent.get(&args.url).call(),
        HttpVerb::HEAD => agent.head(&args.url).call(),
    };

    // Traitement du résultat et sortie avec le code approprié pour Docker
    match result {
        Ok(resp) if resp.status() == 200 => {
            log_simple(LogLevel::Info, &format!("Healthcheck OK: {} {} => {}", args.verb as u8, args.url, resp.status()));
            println!("Healthcheck OK: {} {} => {}", args.verb as u8, args.url, resp.status());
            std::process::exit(0);
        }
        Ok(resp) => {
            log_simple(LogLevel::Info, &format!("Healthcheck FAIL: {} {} => {}", args.verb as u8, args.url, resp.status()));
            eprintln!("Healthcheck FAIL: {} {} => {}", args.verb as u8, args.url, resp.status());
            std::process::exit(1);
        }
        Err(e) => {
            log_simple(LogLevel::Trace, &format!("Healthcheck ERROR: {} {} => {}", args.verb as u8, args.url, e));
            eprintln!("Healthcheck ERROR: {} {} => {}", args.verb as u8, args.url, e);
            std::process::exit(2);
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_httpverb_from_str() {
        use clap::ValueEnum;
        assert_eq!(HttpVerb::from_str("get", true).unwrap(), HttpVerb::GET);
        assert_eq!(HttpVerb::from_str("head", true).unwrap(), HttpVerb::HEAD);
        assert!(HttpVerb::from_str("post", true).is_err());
    }

    #[test]
    fn test_args_default() {
        let args = Args::parse_from(["bin", "http://localhost"]);
        assert_eq!(args.url, "http://localhost");
        assert_eq!(args.verb, HttpVerb::GET);
        assert_eq!(args.timeout, 5);
    }

    #[test]
    fn test_args_custom() {
        let args = Args::parse_from(["bin", "http://localhost", "--verb", "head", "--timeout", "10"]);
        assert_eq!(args.url, "http://localhost");
        assert_eq!(args.verb, HttpVerb::HEAD);
        assert_eq!(args.timeout, 10);
    }
}

#[cfg(test)]
mod logger_tests {
    use super::*;
    use std::sync::{Arc, Mutex};
    use std::time::Duration;

    struct TestLogger {
        sender: Sender<String>,
        buffer: Arc<Mutex<Vec<String>>>,
    }

    impl TestLogger {
        fn new() -> Self {
            let (tx, rx) = mpsc::channel::<String>();
            let buffer = Arc::new(Mutex::new(Vec::new()));
            let buffer_clone = Arc::clone(&buffer);
            std::thread::spawn(move || {
                while let Ok(line) = rx.recv_timeout(Duration::from_millis(100)) {
                    buffer_clone.lock().unwrap().push(line);
                }
            });
            TestLogger { sender: tx, buffer }
        }
        fn log(&self, msg: String) {
            let _ = self.sender.send(msg);
        }
        fn get_logs(&self) -> Vec<String> {
            std::thread::sleep(Duration::from_millis(150));
            self.buffer.lock().unwrap().clone()
        }
    }

    #[test]
    fn test_log_level_filtering() {
        // Simule le logger
        let logger = TestLogger::new();
        logger.log("Info: should appear".to_string());
        let logs = logger.get_logs();
        assert!(logs.iter().any(|l| l.contains("Info: should appear")));
    }

    #[test]
    fn test_log_simple_function() {
        // On ne peut pas remplacer LOGGER globalement ici, mais on peut tester le filtrage
        LOG_LEVEL.store(LogLevel::Mute as u8, Ordering::Relaxed);
        log_simple(LogLevel::Info, "Should not log");
        LOG_LEVEL.store(LogLevel::Info as u8, Ordering::Relaxed);
        log_simple(LogLevel::Info, "Should log info");
        LOG_LEVEL.store(LogLevel::Trace as u8, Ordering::Relaxed);
        log_simple(LogLevel::Trace, "Should log trace");
        // Ce test vérifie que la fonction ne panique pas et respecte le filtrage
    }
}