
FROM maven:3.9.6-eclipse-temurin-21-alpine as build
WORKDIR /app
COPY ../sensor-core-lib /app/sensor-core-lib
COPY ../sensor-member-lib /app/sensor-member-lib
COPY ../sensor-internal-apis-lib /app/sensor-internal-apis-lib
RUN mvn -f /app/sensor-core-lib clean install -DskipTests
RUN mvn -f /app/sensor-member-lib clean install -DskipTests
RUN mvn -f /app/sensor-internal-apis-lib clean install -DskipTests

COPY ../sensor-api /app/sensor-api
RUN mvn -f /app/sensor-api clean install -DskipTests

FROM openjdk:21
COPY  --from=build /app/sensor-api/target/sensor-api-*.jar api.jar
COPY docker/data/config/api.application.yml /sensor/api/application.yml
COPY docker/data/firmwares /sensor/firmwares
ENTRYPOINT ["java","-jar","api.jar","--spring.config.location=file:/sensor/api/application.yml"]