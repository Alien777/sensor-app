package pl.lasota.sensor.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;


@SpringBootApplication(scanBasePackages = {"pl.lasota.sensor.core", "pl.lasota.sensor.api"})
@EntityScan(value = {"pl.lasota.sensor.core.models"})
@EnableJpaRepositories("pl.lasota.sensor.core.repository")
public class SensorApi {
    public static void main(String[] args) {
        SpringApplication.run(SensorApi.class, args);
    }
}