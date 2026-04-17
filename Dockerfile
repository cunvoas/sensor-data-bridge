# ---------- Build ----------
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Installer git (important pour ton plugin)
RUN apt-get update && apt-get install -y git

# Cloner le projet
RUN git clone https://github.com/MathiasVadot/sensor-data-bridge.git .

# Rendre gradlew exécutable
RUN chmod +x gradlew

# Supprimer le plugin git-version (qui casse sans .git)
RUN sed -i '/com.palantir.git-version/d' sensor-data-bridge/build.gradle

# Build du bon module
RUN ./gradlew :sensor-data-bridge:installDist -x test --no-daemon


# ---------- Runtime ----------
FROM  bellsoft/liberica-openjdk-alpine-musl:25-cds

WORKDIR /app


# Copier le build
COPY --chown=1001:1001 --from=builder /app/sensor-data-bridge/build/install/sensor-data-bridge /app
COPY --chown=1001:1001 secret.env .env

# Remove write permissions from copied files for security
RUN chmod 554 /app/* && chmod 444 /app/.env

USER 1001:1001

#EXPOSE 8980


ENTRYPOINT ["bin/sensor-data-bridge"]
