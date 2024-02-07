package pl.lasota.sensor.api.mqtt;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.service.DeviceService;

@Component
@RequiredArgsConstructor
@Slf4j
public class Utils {

    private final MqttMessagePublish mqttMessagePublish;
    private final DeviceService deviceService;

    public void sendConfig(MessageFrame request) throws Exception {
        DeviceConfig lastDeviceConfig = deviceService.currentDeviceConfig(request.getDeviceKey(), request.getMemberKey());

        MessageFrame response = MessageFrame.createConfigPayload(lastDeviceConfig.getId(),
                lastDeviceConfig.getForVersion(),
                request.getDeviceKey(),
                request.getMemberKey(),
                lastDeviceConfig.getConfig());

        mqttMessagePublish.publish(response);
    }
}
