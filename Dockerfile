# === СТАДИЯ СБОРКИ ===
FROM cimg/openjdk:21.0-node AS builder

USER root

WORKDIR /opt
RUN curl -sSL https://services.gradle.org/distributions/gradle-8.13-bin.zip -o gradle.zip \
    && unzip gradle.zip \
    && rm gradle.zip \
    && ln -s /opt/gradle-8.13/bin/gradle /usr/bin/gradle

ENV PATH="/opt/gradle-8.13/bin:$PATH"

WORKDIR /home/circleci/app

# Копируем только package.json и lock-файл
COPY frontend/package*.json ./frontend/

WORKDIR /home/circleci/app/frontend
RUN npm install --loglevel info --progress

# Копируем весь проект и сразу задаём владельца
WORKDIR /home/circleci/app
COPY --chown=circleci:circleci . .

# 🛠️ Создаём .gradle и build директории с нужными правами
RUN mkdir -p /home/circleci/app/.gradle \
    && mkdir -p /home/circleci/app/build/reports/problems \
    && chown -R circleci:circleci /home/circleci/app/.gradle \
    && chown -R circleci:circleci /home/circleci/app/build

USER circleci
WORKDIR /home/circleci/app

# 🏗️ Сборка jar
RUN gradle bootJar --no-daemon

# === СТАДИЯ РАНТАЙМА ===
FROM eclipse-temurin:21-jre

RUN useradd -ms /bin/bash springuser
USER springuser

WORKDIR /home/springuser
COPY --from=builder /home/circleci/app/build/libs/app.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
