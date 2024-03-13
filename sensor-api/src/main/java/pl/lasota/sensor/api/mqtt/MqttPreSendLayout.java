package pl.lasota.sensor.api.mqtt;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.models.mqtt.payload.to.PwmPayload;
import pl.lasota.sensor.core.service.DeviceService;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttPreSendLayout {

    private final MqttMessagePublish mqttMessagePublish;
    private final DeviceService deviceService;

    public void sendConfig(String memberKey, String deviceId) throws Exception {
        DeviceConfig lastDeviceConfig = deviceService.currentDeviceConfig(memberKey, deviceId);
        MessageFrame mf = MessageFrame.factoryConfigPayload(lastDeviceConfig.getId(),
                lastDeviceConfig.getForVersion(),
                deviceId,
                memberKey,
                lastDeviceConfig.getConfig());
        mqttMessagePublish.publish(mf);
    }

    public void sendPwm(String memberKey, String deviceId, int pin, long value) throws Exception {
        DeviceConfig lastDeviceConfig = deviceService.currentDeviceConfig(memberKey, deviceId);
        MessageFrame mf = MessageFrame.factoryPwmPayload(lastDeviceConfig.getId(),
                lastDeviceConfig.getForVersion(),
                deviceId,
                memberKey,
                new PwmPayload(pin, value));
        mqttMessagePublish.publish(mf);
    }
}
