# ---------- Stage 1: Runtime ----------
FROM openjdk:17-jdk-slim

# Crée un dossier pour l'application
WORKDIR /app

# Copie le JAR déjà compilé depuis ton projet local
# Assure-toi que le JAR existe avant de build l'image
COPY sensor-data-bridge/build/libs/sensor-data-bridge.jar ./sensor-data-bridge.jar

# Expose le port que ton application utilise (à adapter)
EXPOSE 8080

# Commande pour lancer l'application
CMD ["java", "-jar", "sensor-data-bridge.jar"]
