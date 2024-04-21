FROM openjdk:21
COPY ../sensor-flows/target/sensor-*.jar flow.jar
COPY docker/data/config/flows.application.yml /sensor/flow/application.yml
ENTRYPOINT ["java","-jar","flow.jar","--spring.config.location=file:/sensor/flow/application.yml"]