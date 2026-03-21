# Debian-based JDK pour Render
FROM eclipse-temurin:21-jdk

LABEL maintainer="MV"
LABEL org.opencontainers.image.source="https://github.com/MathiasVadot/sensor-data-bridge/"
LABEL org.opencontainers.image.description="Receives sensor data over TTN and forwards it to sensor.community"
LABEL org.opencontainers.image.licenses="MIT"

# Installer outils nécessaires
RUN apt-get update && apt-get install -y bash unzip curl git && rm -rf /var/lib/apt/lists/*

# Copier projet
WORKDIR /opt/sensor-data-bridge
COPY . .

# Rendre gradlew exécutable
RUN chmod +x gradlew

# Build du jar sans tests pour éviter blocage cloud
RUN ./gradlew build -x test --no-daemon

# Exposer le port
EXPOSE 8080

# Entrypoint vers le jar
ENTRYPOINT ["java","-jar","build/libs/sensor-data-bridge.jar"]
