package pl.lasota.sensor.api.mqtt.filter.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.filter.Chain;
import pl.lasota.sensor.api.mqtt.filter.Filter;
import pl.lasota.sensor.api.mqtt.filter.Utils;
import pl.lasota.sensor.core.model.Message;
import pl.lasota.sensor.core.service.SensorService;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class SaveSensorFilter implements Filter<Message, Message> {

    public static final int MAC_SIZE = 12;

    private final SensorService sensorService;
    private final Utils utils;

    @Override
    public void execute(Message request, Chain<Message> chain) {
        if (request.getDeviceKey().trim().isBlank() || request.getDeviceKey().length() != MAC_SIZE) {
            log.info("Not save sensor because mac is wrong {} ", request.getDeviceKey());
            return;
        }

        if (!sensorService.isSensorExisting(request.getMemberKey(), request.getDeviceKey())) {
            sensorService.insertSensor(request.getMemberKey(), request.getDeviceKey());
            utils.sendConfig(request);
        }

        chain.doFilter(request);
    }
}
