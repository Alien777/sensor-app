package pl.lasota.sensor.flows;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"pl.lasota.sensor.core"})
@EntityScan(value = {"pl.lasota.sensor.core.models"})
@EnableJpaRepositories("pl.lasota.sensor.core.repository")
public class SensorFlows {
    public static void main(String[] args) {
        SpringApplication.run(SensorFlows.class, args);
    }
}
