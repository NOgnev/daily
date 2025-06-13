FROM cimg/openjdk:21.0-node AS builder

USER root

WORKDIR /opt

RUN curl -sSL https://services.gradle.org/distributions/gradle-8.13-bin.zip -o gradle.zip \
    && unzip gradle.zip \
    && rm gradle.zip \
    && ln -s /opt/gradle-8.13/bin/gradle /usr/bin/gradle

ENV PATH="/opt/gradle-8.13/bin:$PATH"

USER circleci

WORKDIR /home/circleci/app

COPY . .

USER root
RUN chown -R circleci:circleci /home/circleci/app
USER circleci

RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre

RUN useradd -ms /bin/bash springuser
USER springuser

WORKDIR /home/springuser
COPY --from=builder /home/circleci/app/build/libs/app.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
