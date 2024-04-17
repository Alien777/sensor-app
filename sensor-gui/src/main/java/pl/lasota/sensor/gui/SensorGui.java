package pl.lasota.sensor.gui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"pl.lasota.sensor.internal.apis", "pl.lasota.sensor.member", "pl.lasota.sensor.gui"},
        exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
@EntityScan(value = {"pl.lasota.sensor.member.entities"})
@EnableJpaRepositories(value = {"pl.lasota.sensor.member.repositories"})
@EnableFeignClients(basePackages = {"pl.lasota.sensor.internal.apis.api"})
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SensorGui {
    public static void main(String[] args) {
        SpringApplication.run(SensorGui.class, args);
    }
}
