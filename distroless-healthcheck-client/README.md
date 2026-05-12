# micro-healthcheck-client

Micro-client HTTP/HTTPS en Rust pour healthcheck dans des containers Docker distroless.

Inspiré de [docker-health-checks-on-distroless-containers](https://natalia.dev/blog/2023/03/docker-health-checks-on-distroless-containers-with-rust/).


## Fonctionnalités
- Effectue des requêtes HTTP GET ou HEAD sur une URL donnée
- Timeout configurable
- Retourne un code de sortie adapté pour Docker HEALTHCHECK
- Binaire statique compatible distroless

## Utilisation

### Compilation

```sh
cargo build --release
```
Le binaire se trouve dans `target/release/micro-healthcheck-client`.

### Exemple d'exécution

```sh
./distroless-healthcheck-client --verb get https://www.google.com
./distroless-healthcheck-client --verb head --timeout 2 https://www.google.com
```

### Codes de sortie
- 0 : Succès (HTTP 200)
- 1 : Réponse HTTP reçue mais code ≠ 200
- 2 : Erreur réseau ou autre

### Options
- `--verb`, `-v` : Verbe HTTP à utiliser (`get` ou `head`, défaut : `get`)
- `--timeout`, `-t` : Timeout en secondes (défaut : 5)
- `--log`, `-l` : Niveau de log (`TRACE`, `INFO`, `MUTE`). Prioritaire sur la variable d'environnement `LOG_LEVEL`.
- `--name`, `-n` : Nom du container pour le fichier de log (défaut : valeur de `/etc/hostname` ou `unknown`)

### Exemple dans un Dockerfile distroless

```dockerfile
# ... existing code ...
```

Ajoutez dans votre Dockerfile :
```dockerfile
HEALTHCHECK CMD ["/distroless-healthcheck-client", "--name", "mon-service", "https://votre-service/health"]
```

## Logging

Le client intègre un logger asynchrone qui écrit simultanément sur la console et dans un fichier de log localisé dans `/var/log/healthcheck-{containerName}.log`.

### Configuration du fichier de log
Le nom du conteneur utilisé dans le nom du fichier est déterminé par :
1. L'option `--name` ou `-n` en ligne de commande.
2. Si absente, le contenu du fichier `/etc/hostname` (souvent l'ID du conteneur Docker).
3. Si la lecture échoue, "unknown" est utilisé.

### Niveaux de log
- **INFO** (défaut) : affiche les logs principaux (succès/échec)
- **TRACE** : affiche aussi les erreurs détaillées (ex : erreurs réseau)
- **MUTE** : désactive toute sortie log (hors stdout/stderr du healthcheck)

Le niveau de log peut être défini :
- en ligne de commande : `--log TRACE|INFO|MUTE` (prioritaire)
- ou via la variable d'environnement `LOG_LEVEL`

Exemples :
```sh
./distroless-healthcheck-client --log TRACE ...
LOG_LEVEL=MUTE ./distroless-healthcheck-client ...
```

### Exemple de log (Console et Fichier)

```
[LOG] Info: Démarrage du healthcheck
[LOG] Info: Healthcheck OK: 0 https://www.google.com => 200
[LOG] Trace: Healthcheck ERROR: 0 https://www.google.com => ...
```

## Licence
GPT v3