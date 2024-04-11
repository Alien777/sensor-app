FROM openjdk:21
COPY target/*.jar flow.jar
COPY ../sensor-flows/src/main/resources/application.yml /sensor/flow/application.yml
ENTRYPOINT ["java","-jar","flow.jar","--spring.config.location=file:/sensor/flow/application.yml"]
