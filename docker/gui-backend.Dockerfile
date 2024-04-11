FROM openjdk:21
COPY target/*.jar gui_backend.jar
COPY ../sensor-gui/src/main/resources/application.yml /sensor/gui_backend/application.yml
ENTRYPOINT ["java","-jar","flow.jar","--spring.config.location=file:/app/gui_backend/application.yml"]

FROM openjdk:21
COPY target/*.jar gui_backend.jar
COPY ../sensor-gui/src/main/resources/application.yml /sensor/gui_backend/application.yml
ENTRYPOINT ["java","-jar","flow.jar","--spring.config.location=file:/app/gui_backend/application.yml"]

