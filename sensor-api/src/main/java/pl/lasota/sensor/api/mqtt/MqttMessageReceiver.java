package pl.lasota.sensor.api.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.filter.FilterChain;
import pl.lasota.sensor.api.mqtt.filter.filters.*;
import pl.lasota.sensor.api.mqtt.model.MessagePayload;
import pl.lasota.sensor.core.exceptions.FilterExecuteException;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttMessageReceiver {

    private final ApplicationContext ac;

    @Async
    public void received(MessagePayload messagePayload) {

        FilterChain filterChain = new FilterChain()
                .addFilter(ac.getBean(MessageMappingFilter.class))
                .addFilter(ac.getBean(IsExistMemberFilter.class))
                .addFilter(ac.getBean(SaveSensorFilter.class))
                .addFilter(ac.getBean(SaveSensorValueFilter.class));

        log.info("Received messageFrame from {}, {}", messagePayload.topic(), messagePayload.messageFrame());
        try {
            filterChain.doFilter(messagePayload.messageFrame());
        } catch (FilterExecuteException e) {
            log.error("Occurred problem with execute chain", e);
        }
    }
}