# === BUILD STAGE ===
FROM node:20-bullseye AS builder

# Устанавливаем Gradle
RUN apt-get update && apt-get install -y unzip curl openjdk-21-jdk \
 && curl -sSL https://services.gradle.org/distributions/gradle-8.13-bin.zip -o gradle.zip \
 && unzip gradle.zip -d /opt/gradle \
 && ln -s /opt/gradle/gradle-8.13/bin/gradle /usr/bin/gradle \
 && rm gradle.zip

ENV GRADLE_HOME=/opt/gradle/gradle-8.13
ENV PATH="${GRADLE_HOME}/bin:${PATH}"

WORKDIR /app

# Копируем только package.json и устанавливаем зависимости
COPY frontend/package*.json ./frontend/
WORKDIR /app/frontend
RUN npm install --loglevel info --progress=false

# Копируем весь проект
WORKDIR /app
COPY . .

# Сборка jar
RUN gradle bootJar --no-daemon

# === RUNTIME STAGE ===
FROM eclipse-temurin:21-jre

RUN useradd -ms /bin/bash springuser
USER springuser

WORKDIR /home/springuser
COPY --from=builder /app/build/libs/app.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
