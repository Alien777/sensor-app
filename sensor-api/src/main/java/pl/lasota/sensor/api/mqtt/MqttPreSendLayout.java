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
public class MqttPreSendLayout {

    private final MqttMessagePublish mqttMessagePublish;
    private final DeviceService deviceService;

    public void sendConfig(String memberKey, String deviceKey) throws Exception{
        DeviceConfig lastDeviceConfig = deviceService.currentDeviceConfig(memberKey, deviceKey);

        MessageFrame response = MessageFrame.factoryConfigPayload(lastDeviceConfig.getId(),
                lastDeviceConfig.getForVersion(),
                deviceKey,
                memberKey,
                lastDeviceConfig.getConfig());

        mqttMessagePublish.publish(response);
    }

    public void sendPwm(String memberKey, String deviceKey, int pin, long value) {
//        MessageFrame.factoryConfigPayload()
    }
}
