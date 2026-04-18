-- Création explicite du user et de la base ttn en UTF8
DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_user WHERE usename = 'ttn_user') THEN
      CREATE USER ttn_user WITH PASSWORD 'CHANGE_ME_WITH_LONG_PASSWORD';
   END IF;
END$$;

-- Crée la base ttn en UTF8 si elle n'existe pas déjà
DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'dust_sensors') THEN
      CREATE DATABASE dust_sensors WITH OWNER = ttn_user ENCODING = 'UTF8';
   END IF;
END$$;


-- Création de l'utilisateur en lecture seule
CREATE USER ttn_reader WITH PASSWORD 'motdepassefort';

-- Révocation de tous les droits par défaut
REVOKE ALL ON SCHEMA public FROM ttn_reader;
REVOKE ALL ON ALL TABLES IN SCHEMA public FROM ttn_reader;

-- Attribution du droit SELECT sur toutes les tables existantes
GRANT CONNECT ON DATABASE dust_sensors TO ttn_reader;
GRANT USAGE ON SCHEMA public TO ttn_reader;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO ttn_reader;

-- Pour que les futurs tables soient aussi accessibles en lecture :
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO ttn_reader;

