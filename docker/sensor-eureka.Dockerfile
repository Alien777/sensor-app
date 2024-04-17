FROM openjdk:21
COPY ../sensor-eureka/target/sensor-eureka-*.jar eureka.jar
COPY docker/data/config/eureka.application.yml /sensor/eureka/application.yml
ENTRYPOINT ["java","-jar","eureka.jar","--spring.config.location=file:/sensor/eureka/application.yml"]