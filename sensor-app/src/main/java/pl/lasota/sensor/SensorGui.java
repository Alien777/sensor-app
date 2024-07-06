package pl.lasota.sensor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"pl.lasota.sensor"},
        exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
@EntityScan(value = {"pl.lasota.sensor.entities"})
@EnableJpaRepositories(value = {"pl.lasota.sensor.member.repositories",
        "pl.lasota.sensor.device.services.repositories",
        "pl.lasota.sensor.flow.services.repositories"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
public class SensorGui {
    public static void main(String[] args) {
        SpringApplication.run(SensorGui.class, args);
    }
}
