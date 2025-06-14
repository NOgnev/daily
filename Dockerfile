# === BUILD STAGE ===
FROM node:20-bullseye AS frontend

WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install --loglevel info --progress=false

# === JAVA + GRADLE STAGE ===
FROM gradle:8.13.0-jdk21 AS builder

WORKDIR /app

# Копируем зависимости node_modules из предыдущей стадии
COPY --from=frontend /app/frontend/node_modules ./frontend/node_modules
COPY . .

# Сборка backend
RUN gradle bootJar --no-daemon

# === RUNTIME STAGE ===
FROM eclipse-temurin:21-jre

RUN useradd -ms /bin/bash springuser
USER springuser

WORKDIR /home/springuser
COPY --from=builder /app/build/libs/app.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
