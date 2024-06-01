package pl.lasota.sensor.device.services.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.exceptions.SensorApiException;
import pl.lasota.sensor.payload.MessagePayload;


@Component
@Slf4j
@RequiredArgsConstructor
public class MessageProcess {

    private final ApplicationContext ac;

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
        } catch (SensorApiException e) {
            log.error("Occurred problem with execute chain", e);
        }
    }
}
