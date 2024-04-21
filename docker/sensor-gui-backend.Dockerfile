FROM openjdk:21
COPY ../sensor-gui/target/sensor-*.jar gui.jar
COPY docker/data/config/gui.application.yml /sensor/gui/application.yml
ENTRYPOINT ["java","-jar","gui.jar","--spring.config.location=file:/sensor/gui/application.yml"]