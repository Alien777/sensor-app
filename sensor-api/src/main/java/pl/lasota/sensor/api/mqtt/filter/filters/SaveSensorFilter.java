package pl.lasota.sensor.api.mqtt.filter.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.filter.Chain;
import pl.lasota.sensor.api.mqtt.filter.Context;
import pl.lasota.sensor.api.mqtt.filter.Filter;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.service.DeviceService;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class SaveSensorFilter implements Filter<MessageFrame, MessageFrame> {

    private final DeviceService deviceService;

    @Override
    public void execute(MessageFrame request, Context context, Chain<MessageFrame> chain) throws Exception {
        deviceService.insertNewDevice(request.getMemberKey(), request.getDeviceKey(), request.getVersion());
        chain.doFilter(request);
    }
}
