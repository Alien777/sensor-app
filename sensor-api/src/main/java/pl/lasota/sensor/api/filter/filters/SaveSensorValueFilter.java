package pl.lasota.sensor.api.filter.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pl.lasota.sensor.api.filter.Chain;
import pl.lasota.sensor.api.filter.Context;
import pl.lasota.sensor.api.filter.Filter;
import pl.lasota.sensor.api.mqtt.MqttPreSendLayout;
import pl.lasota.sensor.core.apis.FlowsMicroserviceEndpoint;
import pl.lasota.sensor.core.apis.model.flow.FlowSensorAnalogT;
import pl.lasota.sensor.core.apis.model.flow.FlowSensorT;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageType;
import pl.lasota.sensor.core.entities.sensor.AnalogSensor;
import pl.lasota.sensor.core.entities.sensor.Sensor;
import pl.lasota.sensor.core.service.DeviceService;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class SaveSensorValueFilter implements Filter<MessageFrame, MessageFrame> {

    private final DeviceService deviceService;
    private final MqttPreSendLayout mqttPreSendLayout;
    private final FlowsMicroserviceEndpoint flowsMicroserviceEndpoint;
    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    @Override
    public void execute(MessageFrame request, Context context, Chain<MessageFrame> chain) throws Exception {
        Sensor sensor = deviceService.insertSensorReading(request);
        context.setSensor(sensor);
        chain.doFilter(request);
    }

    @Override
    public void postExecute(MessageFrame request, Context context) throws Exception {

        if (request.getMessageType().equals(MessageType.DEVICE_CONNECTED)) {
            mqttPreSendLayout.sendConfig(request.getMemberId(), request.getDeviceId());
        }

        Sensor sensor = context.getSensor();
        if (sensor != null) {
            FlowSensorT flowSensorT = new FlowSensorT().setDeviceId(sensor.getDevice().getId())
                    .setMemberId(request.getDeviceId())
                    .setMessageType(sensor.getMessageType());

            FlowSensorT fT = switch (sensor.getMessageType()) {
                case DEVICE_CONNECTED -> flowSensorT;
                case CONFIG, PWM -> null;
                case ANALOG -> {
                    AnalogSensor aSensor = (AnalogSensor) sensor;
                    yield new FlowSensorAnalogT(flowSensorT).setPin(aSensor.getPin()).setValue(aSensor.getAdcRaw());
                }
            };
            if (fT != null) {
                flowsMicroserviceEndpoint.broadcastValueOfSensorToAllInstances(fT, discoveryClient, restClient);
            }
        }
    }
}
