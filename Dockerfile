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

# Копируем package.json для npm install
COPY frontend/package*.json ./frontend/

WORKDIR /home/circleci/app/frontend
RUN npm install --loglevel info --progress

WORKDIR /home/circleci/app
COPY --chown=circleci:circleci . .

USER circleci
# Указываем кэш gradle в /tmp, чтобы избежать проблем с правами
RUN gradle bootJar --no-daemon -g /tmp/gradle-cache

# === СТАДИЯ РАНТАЙМА ===
FROM eclipse-temurin:21-jre

RUN useradd -ms /bin/bash springuser
USER springuser

WORKDIR /home/springuser
COPY --from=builder /home/circleci/app/build/libs/app.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
