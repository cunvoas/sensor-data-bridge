# ---------- Stage 1: Build ----------
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

RUN apt-get update && apt-get install -y git

RUN git clone https://github.com/MathiasVadot/sensor-data-bridge.git .

RUN chmod +x ./gradlew

# ✅ bon chemin
RUN sed -i '/com.palantir.git-version/d' sensor-data-bridge/build.gradle

# ✅ build du module
RUN ./gradlew :sensor-data-bridge:installDist -x test --no-daemon

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21.0.8_9-jre-alpine

WORKDIR /opt

COPY --from=builder /app/sensor-data-bridge/build/distributions/sensor-data-bridge.tar .

RUN tar -xvf sensor-data-bridge.tar

WORKDIR /opt/sensor-data-bridge

ENTRYPOINT ["/opt/sensor-data-bridge/bin/sensor-data-bridge"]
