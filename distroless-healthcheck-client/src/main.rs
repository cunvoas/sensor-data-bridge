use clap::{Parser, ValueEnum};

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
}

#[derive(Copy, Clone, PartialEq, Eq, PartialOrd, Ord, ValueEnum, Debug)]
enum HttpVerb {
    GET,
    HEAD,
}

fn main() {
    let args = Args::parse();
    let agent = ureq::AgentBuilder::new()
        .timeout(std::time::Duration::from_secs(args.timeout))
        .build();
    let result = match args.verb {
        HttpVerb::GET => agent.get(&args.url).call(),
        HttpVerb::HEAD => agent.head(&args.url).call(),
    };
    match result {
        Ok(resp) if resp.status() == 200 => {
            println!("Healthcheck OK: {} {} => {}", args.verb as u8, args.url, resp.status());
            std::process::exit(0);
        }
        Ok(resp) => {
            eprintln!("Healthcheck FAIL: {} {} => {}", args.verb as u8, args.url, resp.status());
            std::process::exit(1);
        }
        Err(e) => {
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
