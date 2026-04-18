# HOW_TO.md

## Builder l'application Rust

1. Rendez-vous dans le dossier du projet Rust :
   ```sh
   cd rust-collector
   ```
2. Compilez le projet :
   ```sh
   cargo build --release
   ```
3. Pour lancer le serveur en mode développement :
   ```sh
   cargo run
   ```

> **Astuce** : Copiez le fichier `.env.example` en `.env` et adaptez les variables si besoin.

---

## Déployer/modifier l'application avec Docker Compose

1. Rendez-vous dans le dossier `docker` :
   ```sh
   cd docker
   ```
2. Modifiez les fichiers de configuration si nécessaire :
   - `sensor-data-bridge.yaml`
   - `log4j.properties`
   - `log/` (dossier de logs)
3. Pour (re)déployer les services :
   ```sh
   docker-compose up -d --build
   ```
4. Pour voir les logs :
   ```sh
   docker-compose logs -f
   ```
5. Pour arrêter les services :
   ```sh
   docker-compose down
   ```

> **Remarque** :
> - Le fichier `docker-compose.yaml` se trouve dans le dossier `docker`.
> - Adaptez les chemins des volumes si besoin.
