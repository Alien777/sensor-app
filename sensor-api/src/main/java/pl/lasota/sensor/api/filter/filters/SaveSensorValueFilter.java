package pl.lasota.sensor.api.filter.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.filter.Chain;
import pl.lasota.sensor.api.filter.Context;
import pl.lasota.sensor.api.filter.Filter;
import pl.lasota.sensor.api.mqtt.MqttPreSendLayout;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.models.mqtt.payload.MessageType;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.restapi.SensorFlowsHelper;
import pl.lasota.sensor.core.service.DeviceService;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class SaveSensorValueFilter implements Filter<MessageFrame, MessageFrame> {

    private final DeviceService deviceService;
    private final MqttPreSendLayout mqttPreSendLayout;
    private final SensorFlowsHelper sensorFlowsHelper;

    @Override
    public void execute(MessageFrame request, Context context, Chain<MessageFrame> chain) throws Exception {
        Sensor sensor = deviceService.insertSensorReading(request);
        context.setSensor(sensor);
        chain.doFilter(request);
    }

    @Override
    public void postExecute(MessageFrame request, Context context) throws Exception {
        if (request.getMessageType().equals(MessageType.DEVICE_CONNECTED)) {
            mqttPreSendLayout.sendConfig(request.getMemberKey(), request.getDeviceKey());
        }

        Sensor sensor = context.getSensor();
        if (sensor != null) {
            sensorFlowsHelper.sensorValueAllInstances(sensor);
        }
    }
}
