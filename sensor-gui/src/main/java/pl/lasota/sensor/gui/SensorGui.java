package pl.lasota.sensor.gui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(value = {"pl.lasota.sensor.core", "pl.lasota.sensor.gui"})
@EntityScan(value = {"pl.lasota.sensor.core.model"})
@EnableJpaRepositories("pl.lasota.sensor.core.repository")
public class SensorGui {
    public static void main(String[] args) {
        SpringApplication.run(SensorGui.class, args);
    }
}
