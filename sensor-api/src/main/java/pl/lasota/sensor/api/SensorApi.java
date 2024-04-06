package pl.lasota.sensor.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {"pl.lasota.sensor.core", "pl.lasota.sensor.api"})
@EntityScan(value = {"pl.lasota.sensor.core.entities"})
@EnableJpaRepositories("pl.lasota.sensor.core.repository")
@EnableFeignClients(basePackages = {"pl.lasota.sensor.core.apis"})
@EnableDiscoveryClient
public class SensorApi {

    public static void main(String[] args) {
        SpringApplication.run(SensorApi.class, args);
    }
}
