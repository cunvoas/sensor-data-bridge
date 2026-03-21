LABEL maintainer="MV"
LABEL org.opencontainers.image.source="https://github.com/MathiasVadot/sensor-data-bridge/"
LABEL org.opencontainers.image.description="Receives sensor data over TTN and forwards it to sensor.community"
LABEL org.opencontainers.image.licenses="MIT"

# Installer outils nécessaires
RUN apk add --no-cache bash unzip curl git

# Copier projet
WORKDIR /opt/sensor-data-bridge
COPY . .

# Rendre gradlew exécutable et builder le projet
RUN chmod +x gradlew
RUN ./gradlew build

# Exposer le port
EXPOSE 8080

# Entrypoint vers le jar
ENTRYPOINT ["java","-jar","build/libs/sensor-data-bridge.jar"]
