# ---------- Stage 1: Build ----------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Installer git (nécessaire pour le plugin git-version)
RUN apt-get update && apt-get install -y git unzip && rm -rf /var/lib/apt/lists/*

# Cloner le repo COMPLET avec .git
RUN git clone https://github.com/MathiasVadot/sensor-data-bridge.git .

# Donner les droits à gradlew
RUN chmod +x ./gradlew

# Build de la distribution (IMPORTANT)
RUN ./gradlew installDist -x test --no-daemon

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /opt

# Copier la distribution générée
COPY --from=builder /app/sensor-data-bridge/build/distributions/sensor-data-bridge.tar /opt/

# Extraire
RUN tar -xvf sensor-data-bridge.tar && rm sensor-data-bridge.tar

WORKDIR /opt/sensor-data-bridge

ENTRYPOINT ["/opt/sensor-data-bridge/bin/sensor-data-bridge"]
