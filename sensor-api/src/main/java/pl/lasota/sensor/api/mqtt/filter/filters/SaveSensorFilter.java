package pl.lasota.sensor.api.mqtt.filter.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.filter.Chain;
import pl.lasota.sensor.api.mqtt.filter.Filter;
import pl.lasota.sensor.api.mqtt.Utils;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.service.DeviceService;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class SaveSensorFilter implements Filter<MessageFrame, MessageFrame> {

    public static final int MAC_SIZE = 12;

    private final DeviceService deviceService;
    private final Utils utils;

    @Override
    public void execute(MessageFrame request, Chain<MessageFrame> chain) {
        if (request.getDeviceKey().trim().isBlank() || request.getDeviceKey().length() != MAC_SIZE) {
            log.info("Not save sensor because mac is wrong {} ", request.getDeviceKey());
            return;
        }

        if (!deviceService.isDeviceExisting(request.getMemberKey(), request.getDeviceKey())) {
            deviceService.insertDevice(request.getMemberKey(), request.getDeviceKey());
            utils.sendConfig(request);
        }

        chain.doFilter(request);
    }
}
