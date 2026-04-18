-- Création explicite du user et de la base ttn en UTF8
DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_user WHERE usename = 'postgres') THEN
      CREATE USER postgres WITH PASSWORD 'postgres';
   END IF;
END$$;

-- Crée la base ttn en UTF8 si elle n'existe pas déjà
DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'ttn') THEN
      CREATE DATABASE ttn WITH OWNER = postgres ENCODING = 'UTF8' LC_COLLATE = 'C' LC_CTYPE = 'C' TEMPLATE = template0;
   END IF;
END$$;
