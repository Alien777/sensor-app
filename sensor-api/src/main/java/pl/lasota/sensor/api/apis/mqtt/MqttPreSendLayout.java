package pl.lasota.sensor.api.apis.mqtt;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.entities.Device;
import pl.lasota.sensor.api.entities.DeviceConfig;
import pl.lasota.sensor.api.exception.SensorApiException;
import pl.lasota.sensor.api.payload.MessageFrame;
import pl.lasota.sensor.api.payload.to.ForceReadingOfAnalogDataPayload;
import pl.lasota.sensor.api.payload.to.PwmPayload;
import pl.lasota.sensor.api.services.DeviceService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttPreSendLayout {

    private final MqttMessagePublish mqttMessagePublish;
    private final DeviceService deviceService;

    public void sendConfig(String memberId, String deviceId) {
        DeviceConfig lastDeviceConfig = deviceService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            try {
                MessageFrame mf = MessageFrame.factoryConfigPayload(lastDeviceConfig.getId(), lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), lastDeviceConfig.getConfig());
                mqttMessagePublish.publish(mf);
            } catch (Exception e) {
                throw new SensorApiException(e);
            }

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

    public void sendForAnalogData(String memberId, String deviceId, int pin) throws Exception {
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
