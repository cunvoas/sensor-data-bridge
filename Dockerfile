FROM eclipse-temurin:21.0.8_9-jre-alpine

LABEL maintainer="MV"
LABEL org.opencontainers.image.source="https://github.com/MathiasVadot/sensor-data-bridge/"
LABEL org.opencontainers.image.description="Receives sensor data over TTN and forwards it to sensor.community"
LABEL org.opencontainers.image.licenses="MIT"

# Installer bash et unzip pour Gradle
RUN apk add --no-cache bash unzip curl git

# Copier tout le projet dans le conteneur
WORKDIR /opt/sensor-data-bridge
COPY . .

# Rendre gradlew exécutable et builder le projet
RUN chmod +x gradlew
RUN ./gradlew build

# Exposer le port 8080
EXPOSE 8080

# Entrypoint vers le jar généré
ENTRYPOINT ["java","-jar","build/libs/sensor-data-bridge.jar"]
