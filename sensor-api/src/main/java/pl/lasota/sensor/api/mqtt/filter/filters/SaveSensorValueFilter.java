package pl.lasota.sensor.api.mqtt.filter.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.filter.Chain;
import pl.lasota.sensor.api.mqtt.filter.Filter;
import pl.lasota.sensor.api.mqtt.filter.Utils;
import pl.lasota.sensor.core.model.Message;
import pl.lasota.sensor.core.model.MessageType;
import pl.lasota.sensor.core.service.SensorService;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class SaveSensorValueFilter implements Filter<Message, Message> {

    private final SensorService sensorService;
    private final Utils utils;

    @Override
    public void execute(Message request, Chain<Message> chain) {

        if (request.getMessageType().equals(MessageType.DEVICE_CONNECTED)) {
            utils.sendConfig(request);
        }

        sensorService.insertReading(request, request.getMessage());
    }
}
