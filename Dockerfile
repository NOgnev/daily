# === СТАДИЯ СБОРКИ ===
FROM cimg/openjdk:21.0-node AS builder

# Работаем от root для установки gradle и node-зависимостей
USER root

# Устанавливаем Gradle
WORKDIR /opt
RUN curl -sSL https://services.gradle.org/distributions/gradle-8.13-bin.zip -o gradle.zip \
    && unzip gradle.zip \
    && rm gradle.zip \
    && ln -s /opt/gradle-8.13/bin/gradle /usr/bin/gradle

ENV PATH="/opt/gradle-8.13/bin:$PATH"

# Подготовка рабочей директории
WORKDIR /home/circleci/app

# Копируем только package.json + lock для npm install
COPY frontend/package*.json ./frontend/

# Установка frontend-зависимостей (как root)
WORKDIR /home/circleci/app/frontend
RUN npm install --loglevel info --progress

# Копируем остальной проект и сразу задаём владельца
WORKDIR /home/circleci/app
COPY --chown=circleci:circleci . .

# Смена пользователя перед сборкой
USER circleci
RUN gradle bootJar --no-daemon

# === СТАДИЯ РАНТАЙМА ===
FROM eclipse-temurin:21-jre

# Создаём обычного пользователя
RUN useradd -ms /bin/bash springuser
USER springuser

WORKDIR /home/springuser

# Копируем jar-файл из предыдущего этапа
COPY --from=builder /home/circleci/app/build/libs/app.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
