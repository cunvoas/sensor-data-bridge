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

### Exemple dans un Dockerfile distroless

```dockerfile
FROM rust:1.77 as builder
WORKDIR /app
COPY . .
RUN cargo build --release

FROM gcr.io/distroless/cc
COPY --from=builder /app/target/release/distroless-healthcheck-client /distroless-healthcheck-client
ENTRYPOINT ["/distroless-healthcheck-client"]
```

Ajoutez dans votre Dockerfile :
```dockerfile
HEALTHCHECK CMD ["/distroless-healthcheck-client", "--verb", "get", "https://votre-service/health"]
```

## Licence
GPT v3
