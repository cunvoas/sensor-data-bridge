# rust-collector

Collecteur REST pour messages TTN uplink, stockage PostgreSQL

## Fonctionnalités
- Expose un endpoint REST POST `/uplink` pour recevoir des messages TTN uplink au format JSON
- Sauvegarde chaque message dans une table PostgreSQL (`ttn_uplink`)
- Configuration de la base via variables d'environnement ou fichier `.env`

## Exemple de payload JSON
```json
{
  "app_id": "my_app",
  "dev_id": "my_dev",
  "dev_eui": "0011223344556677",
  "raw_payload": {"temperature": 22.5, "humidity": 60},
  "decoded_fields": "{\"temperature\":22.5,\"humidity\":60}",
  "port": 1,
  "rssi": -120.0,
  "snr": 7.5,
  "sf": 12
}
```

## Lancement du serveur
1. Copier `.env.example` en `.env` et adapter les valeurs :
   ```env
   POSTGRES_HOST=localhost
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=postgres
   POSTGRES_DB=ttn
   ```
2. Créer la table PostgreSQL :
   ```sh
   psql -U postgres -d ttn -f ttn_uplink.sql
   ```
3. Compiler et lancer le serveur :
   ```sh
   cargo run
   ```

## Endpoint REST
- **POST** `/uplink`
  - Body : JSON conforme à la structure `TtnUplinkMessage`
  - Réponse : `200 OK` si succès, `500` sinon

## Structure de la table PostgreSQL
Voir `ttn_uplink.sql` :
```sql
CREATE TABLE IF NOT EXISTS ttn_uplink (
    id SERIAL PRIMARY KEY,
    app_id TEXT NOT NULL,
    dev_id TEXT NOT NULL,
    dev_eui TEXT NOT NULL,
    raw_payload JSONB NOT NULL,
    decoded_fields TEXT NOT NULL,
    port INTEGER NOT NULL,
    rssi DOUBLE PRECISION,
    snr DOUBLE PRECISION,
    sf INTEGER
);
```

## Dépendances principales
- [actix-web](https://actix.rs/) : serveur web asynchrone
- [tokio-postgres](https://docs.rs/tokio-postgres/) : client PostgreSQL asynchrone
- [serde](https://serde.rs/) : sérialisation/désérialisation JSON
- [dotenv](https://docs.rs/dotenv/) : gestion des variables d'environnement

## Organisation des tests

Les tests sont séparés du code source principal selon les bonnes pratiques Rust :

- **Tests unitaires** : ils peuvent être placés dans chaque fichier source sous un module `#[cfg(test)] mod tests { ... }` (non utilisé ici pour garder le code source plus lisible).
- **Tests d'intégration** : ils sont placés dans le dossier `tests/` à la racine du projet. Par exemple, les tests pour `TtnUplinkMessage` se trouvent dans `tests/ttn_uplink_message.rs`.

### Pourquoi ce split ?

- Le code source de production (`src/`) ne contient que la logique métier.
- Les tests d'intégration (`tests/`) valident le comportement global et peuvent utiliser l'API publique du crate comme le ferait un utilisateur externe.
- Cela facilite la maintenance, la lisibilité et l'évolution du projet.

### Ajouter ou lancer les tests

- Pour ajouter un test d'intégration, créez un fichier dans `tests/` et importez les modules publics du projet :
  ```rust
  use rust_collector::ttn_uplink_message::TtnUplinkMessage;
  ```
- Pour lancer tous les tests :
  ```sh
  cargo test
  ```

- Le fichier `src/lib.rs` expose les modules nécessaires pour les tests d'intégration.

## Notes
- Le champ `raw_payload` est stocké en JSON natif (jsonb) dans la base.
- Le serveur écoute sur le port 8080 par défaut.
- Le projet est compatible Rust 2021.