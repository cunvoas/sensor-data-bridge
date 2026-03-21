# ---------- Stage 1: Build ----------
FROM eclipse-temurin:21-jdk AS build

LABEL maintainer="MV"
LABEL org.opencontainers.image.source="https://github.com/MathiasVadot/sensor-data-bridge/"
LABEL org.opencontainers.image.description="Receives sensor data over TTN and forwards it to sensor.community"
LABEL org.opencontainers.image.licenses="MIT"

# Installer outils nécessaires pour le build
RUN apt-get update && apt-get install -y bash unzip curl git && rm -rf /var/lib/apt/lists/*

# Définir le répertoire de travail
WORKDIR /opt/sensor-data-bridge

# Copier le projet (sauf .git, inutile pour Docker)
COPY . .

# Rendre gradlew exécutable
RUN chmod +x gradlew

# Build du jar sans tests et en ignorant l'absence de Git
RUN ./gradlew build -x test --no-daemon -Pgit.commit.id.ignore=true --stacktrace --info

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jdk AS runtime

WORKDIR /opt/sensor-data-bridge

# Copier uniquement le jar construit depuis le stage build
COPY --from=build /opt/sensor-data-bridge/build/libs/sensor-data-bridge.jar .

# Exposer le port de l'application
EXPOSE 8080

# Entrypoint pour lancer l'application
ENTRYPOINT ["java","-jar","sensor-data-bridge.jar"]
