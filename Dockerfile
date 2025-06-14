FROM cimg/openjdk:21.0-node AS builder

USER root

WORKDIR /opt
RUN curl -sSL https://services.gradle.org/distributions/gradle-8.13-bin.zip -o gradle.zip \
    && unzip gradle.zip \
    && rm gradle.zip \
    && ln -s /opt/gradle-8.13/bin/gradle /usr/bin/gradle

ENV PATH="/opt/gradle-8.13/bin:$PATH"

# Работаем с приложением
WORKDIR /home/circleci/app

# Копируем только frontend package.json — для кэширования npm install
COPY frontend/package*.json ./frontend/

# ✅ Дадим права пользователю на frontend перед npm install
RUN chown -R circleci:circleci /home/circleci/app

USER circleci
WORKDIR /home/circleci/app/frontend
RUN npm install --loglevel info --progress

# 🔁 Вернёмся и скопируем всё остальное
USER root
WORKDIR /home/circleci/app
COPY . .
RUN chown -R circleci:circleci /home/circleci/app
USER circleci

# Сборка jar
WORKDIR /home/circleci/app
RUN gradle bootJar --no-daemon

# === РАНТАЙМ СТАДИЯ ===
FROM eclipse-temurin:21-jre

RUN useradd -ms /bin/bash springuser
USER springuser

WORKDIR /home/springuser
COPY --from=builder /home/circleci/app/build/libs/app.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
