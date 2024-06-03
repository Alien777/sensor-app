
FROM maven:3.9.6-eclipse-temurin-21-alpine as build
WORKDIR /app

COPY ../sensor-app /app/sensor-app
RUN mvn -f /app/sensor-app clean install -DskipTests

FROM openjdk:21
COPY  --from=build /app/sensor-app/target/sensor-app-*.jar app.jar
COPY docker/data/config/app.application.yml /sensor/app/application.yml
COPY docker/data/firmwares /sensor/firmwares
COPY docker/data/config/voice-model /sensor/voice-model
ENTRYPOINT ["java","-jar","app.jar","--spring.config.location=file:/sensor/app/application.yml"]