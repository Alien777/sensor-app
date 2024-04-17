package pl.lasota.sensor.flows;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"pl.lasota.sensor.internal.apis", "pl.lasota.sensor.member", "pl.lasota.sensor.flows"},
        exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
@EntityScan(value = {"pl.lasota.sensor.member.entities", "pl.lasota.sensor.flows.entities"})
@EnableJpaRepositories(value = {"pl.lasota.sensor.member.repositories", "pl.lasota.sensor.flows.repositories"})
@EnableFeignClients(basePackages = {"pl.lasota.sensor.internal.apis.api"})
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SensorFlows {
    public static void main(String[] args) {
        SpringApplication.run(SensorFlows.class, args);
    }
}
