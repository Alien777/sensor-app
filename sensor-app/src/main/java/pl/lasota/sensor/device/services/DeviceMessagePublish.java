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
import pl.lasota.sensor.payload.to.PingPayload;
import pl.lasota.sensor.payload.to.PwmPayload;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceMessagePublish {

    private final MqttMessagePublish mqttMessagePublish;
    private final DeviceDataService deviceDataService;

    public UUID sendConfig(String memberId, String deviceId) {
        DeviceConfig lastDeviceConfig = deviceDataService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceDataService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            try {
                MessageFrame mf = MessageFrame.factoryConfigPayload(lastDeviceConfig.getId(), lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), lastDeviceConfig.getConfig());
                mqttMessagePublish.publish(mf);
                return UUID.fromString(mf.getRequestId());
            } catch (Exception e) {
                throw new SensorApiException(e);
            }
        }
        return null;
    }

    public UUID sendDigital(String memberId, String deviceId, int pin, int value) throws Exception {
        DeviceConfig lastDeviceConfig = deviceDataService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceDataService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            MessageFrame mf = MessageFrame.factoryDigitalPayload(lastDeviceConfig.getId(), lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), new DigitalPayload(pin, value));
            mqttMessagePublish.publish(mf);
            return UUID.fromString(mf.getRequestId());
        }
        return null;
    }

    public UUID sendPwm(String memberId, String deviceId, int pin, long value, long duration) throws Exception {
        DeviceConfig lastDeviceConfig = deviceDataService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceDataService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            MessageFrame mf = MessageFrame.factoryPwmPayload(lastDeviceConfig.getId(), lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), new PwmPayload(pin, value, duration));
            mqttMessagePublish.publish(mf);
            return UUID.fromString(mf.getRequestId());
        }
        return null;

    }

    public UUID sendForAnalogData(String memberId, String deviceId, int pin) throws Exception {
        DeviceConfig lastDeviceConfig = deviceDataService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceDataService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            MessageFrame mf = MessageFrame.factorySendForAnalogData(lastDeviceConfig.getId(),
                    lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), new ForceReadingOfAnalogDataPayload(pin));
            mqttMessagePublish.publish(mf);
            return UUID.fromString(mf.getRequestId());
        }
        return null;
    }

    public UUID sendForPingData(String memberId, String deviceId) throws Exception {
        DeviceConfig lastDeviceConfig = deviceDataService.currentDeviceConfig(memberId, deviceId);
        Optional<Device> deviceOptional = deviceDataService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            MessageFrame mf = MessageFrame.factorySendPingData(lastDeviceConfig.getId(),
                    lastDeviceConfig.getForVersion(), deviceId, memberId, device.getCurrentDeviceToken().getToken(), new PingPayload());
            mqttMessagePublish.publish(mf);
            return UUID.fromString(mf.getRequestId());
        }
        return null;
    }
}
