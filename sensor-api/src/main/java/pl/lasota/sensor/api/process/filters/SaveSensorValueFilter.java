package pl.lasota.sensor.api.process.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pl.lasota.sensor.api.apis.mqtt.MqttPreSendLayout;
import pl.lasota.sensor.api.entities.AnalogSensor;
import pl.lasota.sensor.api.entities.Sensor;
import pl.lasota.sensor.api.payload.MessageFrame;
import pl.lasota.sensor.api.payload.MessageType;
import pl.lasota.sensor.api.process.Chain;
import pl.lasota.sensor.api.process.FilterContext;
import pl.lasota.sensor.api.process.Filter;
import pl.lasota.sensor.api.services.DeviceService;
import pl.lasota.sensor.internal.apis.api.flows.FlowSensorAnalogI;
import pl.lasota.sensor.internal.apis.api.flows.FlowSensorI;
import pl.lasota.sensor.internal.apis.api.FlowsMicroserviceEndpoint;


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
    public void execute(MessageFrame request, FilterContext filterContext, Chain<MessageFrame> chain) throws Exception {
        Sensor sensor = deviceService.insertSensorReading(request);
        filterContext.setSensor(sensor);
        chain.doFilter(request);
    }

    @Override
    public void postExecute(MessageFrame request, FilterContext filterContext) throws Exception {

        if (request.getMessageType().equals(MessageType.DEVICE_CONNECTED)) {
            mqttPreSendLayout.sendConfig(request.getMemberId(), request.getDeviceId());
        }

        Sensor sensor = filterContext.getSensor();
        if (sensor != null) {
            FlowSensorI flowSensorI = new FlowSensorI().setDeviceId(sensor.getDevice().getId())
                    .setMemberId(request.getDeviceId())
                    .setMessageType(sensor.getMessageType().name());

            FlowSensorI fT = switch (sensor.getMessageType()) {
                case DEVICE_CONNECTED -> flowSensorI;
                case CONFIG, PWM, ANALOG_EXTORT-> null;
                case ANALOG -> {
                    AnalogSensor aSensor = (AnalogSensor) sensor;
                    yield new FlowSensorAnalogI(flowSensorI).setPin(aSensor.getPin()).setValue(aSensor.getAdcRaw());
                }
            };
            if (fT != null) {
                flowsMicroserviceEndpoint.broadcastValueOfSensorToAllInstances(fT, discoveryClient, restClient);
            }
        }
    }
}
