# ---------- Stage 1: Build ----------
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Installer git et unzip pour Gradle wrapper
RUN apt-get update && apt-get install -y git unzip && rm -rf /var/lib/apt/lists/*

# Cloner le projet
RUN git clone https://github.com/MathiasVadot/sensor-data-bridge.git .

# Donner les droits d'exécution au wrapper Gradle
RUN chmod +x ./gradlew

# Build du JAR sans tests
RUN ./gradlew build -x test --no-daemon

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copier le JAR généré depuis l'étape précédente
COPY --from=builder /app/build/libs/sensor-data-bridge.jar ./sensor-data-bridge.jar

# Exposer le port par défaut
EXPOSE 8080

# Lancer l'application
CMD ["java", "-jar", "sensor-data-bridge.jar"]
