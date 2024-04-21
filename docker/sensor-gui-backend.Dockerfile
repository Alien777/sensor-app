FROM maven:3.9.6-eclipse-temurin-21-alpine as build
WORKDIR /app
COPY ../sensor-core-lib /app/sensor-core-lib
COPY ../sensor-member-lib /app/sensor-member-lib
COPY ../sensor-internal-apis-lib /app/sensor-internal-apis-lib
RUN mvn -f /app/sensor-core-lib clean install -DskipTests
RUN mvn -f /app/sensor-member-lib clean install -DskipTests
RUN mvn -f /app/sensor-internal-apis-lib clean install -DskipTests

COPY ../sensor-gui /app/sensor-gui
RUN mvn -f /app/sensor-gui clean install -DskipTests

FROM openjdk:21
COPY --from=build /app/sensor-gui/target/sensor-*.jar gui.jar
COPY docker/data/config/gui.application.yml /sensor/gui/application.yml
ENTRYPOINT ["java","-jar","gui.jar","--spring.config.location=file:/sensor/gui/application.yml"]