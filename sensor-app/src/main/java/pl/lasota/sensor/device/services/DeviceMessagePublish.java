package pl.lasota.sensor.device.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.entities.Device;
import pl.lasota.sensor.entities.DeviceConfig;
import pl.lasota.sensor.exceptions.SensorApiException;
import pl.lasota.sensor.gateway.device.MqttMessagePublish;
import pl.lasota.sensor.payload.MessageFrame;
import pl.lasota.sensor.payload.to.DigitalPayload;
import pl.lasota.sensor.payload.to.ForceReadingOfAnalogDataPayload;
import pl.lasota.sensor.payload.to.PwmPayload;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceMessagePublish {

    private final MqttMessagePublish mqttMessagePublish;
    private final DeviceDataService deviceDataService;

    public void sendConfig(String memberId, String deviceId) {
        DeviceConfig lastDeviceConfig = deviceDataService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceDataService.getDevice(memberId, deviceId);
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

    public void sendDigital(String memberId, String deviceId, int pin, int value) throws Exception {
        DeviceConfig lastDeviceConfig = deviceDataService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceDataService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            MessageFrame mf = MessageFrame.factoryDigitalPayload(lastDeviceConfig.getId(), lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), new DigitalPayload(pin, value));
            mqttMessagePublish.publish(mf);
        }

    }

    public void sendPwm(String memberId, String deviceId, int pin, long value) throws Exception {
        DeviceConfig lastDeviceConfig = deviceDataService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceDataService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            MessageFrame mf = MessageFrame.factoryPwmPayload(lastDeviceConfig.getId(), lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), new PwmPayload(pin, value));
            mqttMessagePublish.publish(mf);
        }

    }

    public void sendForAnalogData(String memberId, String deviceId, int pin) throws Exception {
        DeviceConfig lastDeviceConfig = deviceDataService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceDataService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            MessageFrame mf = MessageFrame.factorySendForAnalogData(lastDeviceConfig.getId(),
                    lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), new ForceReadingOfAnalogDataPayload(pin));
            mqttMessagePublish.publish(mf);
        }
    }
}
