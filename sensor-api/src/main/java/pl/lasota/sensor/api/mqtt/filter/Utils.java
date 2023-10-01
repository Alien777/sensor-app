package pl.lasota.sensor.api.mqtt.filter;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.MqttMessagePublish;
import pl.lasota.sensor.core.exceptions.NotFoundSensorConfigException;
import pl.lasota.sensor.core.model.Message;
import pl.lasota.sensor.core.model.MessageType;
import pl.lasota.sensor.core.model.SensorConfig;
import pl.lasota.sensor.core.service.SensorService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class Utils {

    private final MqttMessagePublish mqttMessagePublish;
    private final SensorService sensorService;

    public void sendConfig(Message request) {
        try {
            SensorConfig lastSensorConfig = sensorService.getLastSensorConfig(request.getDeviceKey());
            Message response = Message.create(request.getDeviceKey(), request.getMemberKey(), MessageType.CONFIG, lastSensorConfig.getConfig());
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
