package pl.lasota.sensor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.filter.FilterChain;
import pl.lasota.sensor.api.filter.filters.*;
import pl.lasota.sensor.api.model.MessagePayload;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.exceptions.FilterExecuteException;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageReceiver {

    private final ApplicationContext ac;

    @Async
    public void received(MessageFrame messagePayload) {
        FilterChain filterChain = new FilterChain()
                .addFilter(ac.getBean(BeforeValidMessageFilter.class))
                .addFilter(ac.getBean(SetUpDeviceFilter.class))
                .addFilter(ac.getBean(AuthFilter.class))
                .addFilter(ac.getBean(SaveSensorValueFilter.class));
        received(messagePayload, filterChain);
    }

    @Async
    public void received(MessagePayload messagePayload) {

        FilterChain filterChain = new FilterChain()
                .addFilter(ac.getBean(MessageMappingFilter.class))
                .addFilter(ac.getBean(BeforeValidMessageFilter.class))
                .addFilter(ac.getBean(SetUpDeviceFilter.class))
                .addFilter(ac.getBean(AuthFilter.class))
                .addFilter(ac.getBean(SaveSensorValueFilter.class));

        received(messagePayload.messageFrame(), filterChain);
    }

    private static void received(Object messageFrame, FilterChain filterChain) {
        log.info("Received messageFrame from {}", messageFrame);
        try {
            filterChain.doFilter(messageFrame);
        } catch (FilterExecuteException e) {
            log.error("Occurred problem with execute chain", e);
        }
    }
}
