
FROM maven:3.9.6-eclipse-temurin-22 as build
WORKDIR /app

COPY ../sensor-app /app/sensor-app
RUN mvn -f /app/sensor-app clean install -DskipTests


FROM openjdk:22-jdk-bullseye
COPY  --from=build /app/sensor-app/target/sensor-app-*.jar app.jar
RUN apt-get update && \
    apt-get install -y curl unzip python3 python3-pip && \
    ln -s /usr/bin/python3 /usr/bin/python
COPY docker/data/config/app.application.yml /sensor/app/application.yml
COPY firmwares /sensor/firmwares
COPY docker/data/config/voice-model /sensor/voice-model
ENTRYPOINT ["java","--enable-preview","-jar","app.jar","--spring.config.location=file:/sensor/app/application.yml"]