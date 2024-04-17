FROM openjdk:21
COPY ../sensor-api/target/sensor-api-*.jar api.jar
COPY docker/data/config/api.application.yml /sensor/api/application.yml
COPY docker/data/firmwares /sensor/firmwares
ENTRYPOINT ["java","-jar","api.jar","--spring.config.location=file:/sensor/api/application.yml"]