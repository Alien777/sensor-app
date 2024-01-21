package pl.lasota.sensor.api.mqtt.filter.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.filter.Chain;
import pl.lasota.sensor.api.mqtt.filter.Filter;
import pl.lasota.sensor.api.mqtt.Utils;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.models.MessageType;
import pl.lasota.sensor.core.service.DeviceService;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class SaveSensorValueFilter implements Filter<MessageFrame, MessageFrame> {

    private final DeviceService deviceService;
    private final Utils utils;

    @Override
    public void execute(MessageFrame request, Chain<MessageFrame> chain) {

        if (request.getMessageType().equals(MessageType.DEVICE_CONNECTED)) {
            utils.sendConfig(request);
        }

        try {
            deviceService.insertReading(request);
        } catch (JsonProcessingException e) {
            log.error("Problem with save sensor value ", e);
        }
    }
}
