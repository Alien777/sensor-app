package pl.lasota.sensor.internal.apis.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import pl.lasota.sensor.internal.apis.security.InternalClientInterceptorConfiguration;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final InternalClientInterceptorConfiguration configuration;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(configuration);
        return restTemplate;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder().requestInterceptor(configuration).build();
    }

}
