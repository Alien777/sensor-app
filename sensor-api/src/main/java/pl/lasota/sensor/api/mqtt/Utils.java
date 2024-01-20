package pl.lasota.sensor.api.mqtt;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.core.exceptions.NotFoundSensorConfigException;
import pl.lasota.sensor.core.mqttPayloads.MessageFrame;
import pl.lasota.sensor.core.model.MessageType;
import pl.lasota.sensor.core.model.device.DeviceConfig;
import pl.lasota.sensor.core.service.DeviceService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class Utils {

    private final MqttMessagePublish mqttMessagePublish;
    private final DeviceService deviceService;

    public void sendConfig(MessageFrame request) {
        try {
            DeviceConfig lastDeviceConfig = deviceService.getLastDeviceConfig(request.getDeviceKey());
            MessageFrame response = MessageFrame.createConfigPayload(request.getDeviceKey(), request.getMemberKey(),lastDeviceConfig.getConfig());
            mqttMessagePublish.publish(response);
        } catch (MqttException e) {
            log.error("Problem with send config file", e);
        } catch (IOException e) {
            log.error("Problem with read config file", e);
        } catch (NotFoundSensorConfigException e) {
            log.warn("Not found configuration");
        }
    }
}
