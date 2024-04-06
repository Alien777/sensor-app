package pl.lasota.sensor.flows;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"pl.lasota.sensor.flows","pl.lasota.sensor.core"})
@EntityScan(value = {"pl.lasota.sensor.core.entities"})
@EnableJpaRepositories(value = {"pl.lasota.sensor.core.repository","pl.lasota.sensor.flows.service"})
@EnableFeignClients(basePackages = {"pl.lasota.sensor.core.apis"})
public class SensorFlows {
    public static void main(String[] args) {
        SpringApplication.run(SensorFlows.class, args);
    }
}
