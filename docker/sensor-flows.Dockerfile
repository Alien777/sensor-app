FROM maven:3.9.6-eclipse-temurin-21-alpine as build
WORKDIR /app
COPY ../sensor-core-lib /app/sensor-core-lib
COPY ../sensor-member-lib /app/sensor-member-lib
COPY ../sensor-internal-apis-lib /app/sensor-internal-apis-lib
RUN mvn -f /app/sensor-core-lib clean install -DskipTests
RUN mvn -f /app/sensor-member-lib clean install -DskipTests
RUN mvn -f /app/sensor-internal-apis-lib clean install -DskipTests

COPY ../sensor-flows /app/sensor-flows
RUN mvn -f /app/sensor-flows clean install -DskipTests

FROM openjdk:21
COPY  --from=build /app/sensor-flows/target/sensor-*.jar flow.jar
COPY docker/data/config/flows.application.yml /sensor/flow/application.yml
ENTRYPOINT ["java","-jar","flow.jar","--spring.config.location=file:/sensor/flow/application.yml"]