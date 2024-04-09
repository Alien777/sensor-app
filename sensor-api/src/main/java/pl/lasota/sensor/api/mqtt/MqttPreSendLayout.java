package pl.lasota.sensor.api.mqtt;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.core.entities.device.Device;
import pl.lasota.sensor.core.entities.device.DeviceConfig;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.entities.mqtt.payload.to.ForceReadingOfAnalogDataPayload;
import pl.lasota.sensor.core.entities.mqtt.payload.to.PwmPayload;
import pl.lasota.sensor.core.service.DeviceService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttPreSendLayout {

    private final MqttMessagePublish mqttMessagePublish;
    private final DeviceService deviceService;

    public void sendConfig(String memberId, String deviceId) throws Exception {
        DeviceConfig lastDeviceConfig = deviceService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            MessageFrame mf = MessageFrame.factoryConfigPayload(lastDeviceConfig.getId(), lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), lastDeviceConfig.getConfig());
            mqttMessagePublish.publish(mf);
        }

    }

    public void sendPwm(String memberId, String deviceId, int pin, long value) throws Exception {
        DeviceConfig lastDeviceConfig = deviceService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            MessageFrame mf = MessageFrame.factoryPwmPayload(lastDeviceConfig.getId(), lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), new PwmPayload(pin, value));
            mqttMessagePublish.publish(mf);
        }

    }

    public void sendForAnalogData(String memberId, String deviceId, int pin) throws Exception{
        DeviceConfig lastDeviceConfig = deviceService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            MessageFrame mf = MessageFrame.factorySendForAnalogData(lastDeviceConfig.getId(),
                    lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), new ForceReadingOfAnalogDataPayload(pin));
            mqttMessagePublish.publish(mf);
        }
    }
}
