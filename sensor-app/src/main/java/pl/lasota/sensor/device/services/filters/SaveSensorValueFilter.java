package pl.lasota.sensor.device.services.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.bus.FlowSensorIInputStreamBus;
import pl.lasota.sensor.device.services.DeviceDataService;
import pl.lasota.sensor.device.services.DeviceMessagePublish;
import pl.lasota.sensor.entities.AnalogSensor;
import pl.lasota.sensor.entities.Sensor;
import pl.lasota.sensor.exceptions.SensorApiException;
import pl.lasota.sensor.flow.model.FlowSensorAnalogI;
import pl.lasota.sensor.flow.model.FlowSensorI;
import pl.lasota.sensor.payload.MessageFrame;
import pl.lasota.sensor.payload.MessageType;

import java.io.IOException;


@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class SaveSensorValueFilter implements Filter<MessageFrame, MessageFrame> {

    private final DeviceDataService deviceDataService;
    private final DeviceMessagePublish deviceMessagePublish;
    private final FlowSensorIInputStreamBus flowApi;

    @Override
    public void execute(MessageFrame request, FilterContext filterContext, Chain<MessageFrame> chain) throws Exception {
        Sensor sensor = deviceDataService.insertSensorReading(request);
        filterContext.setSensor(sensor);
        chain.doFilter(request);
    }

    @Override
    public void postExecute(MessageFrame request, FilterContext filterContext) throws Exception {

        if (request.getMessageType().equals(MessageType.DEVICE_CONNECTED)) {
            deviceMessagePublish.sendConfig(request.getMemberId(), request.getDeviceId());
        }

        Sensor sensor = filterContext.getSensor();
        if (sensor != null) {
            FlowSensorI flowSensorI = new FlowSensorI().setDeviceId(sensor.getDevice().getId())
                    .setMemberId(request.getMemberId())
                    .setMessageType(sensor.getMessageType().name());

            FlowSensorI fT = switch (sensor.getMessageType()) {
                case DEVICE_CONNECTED -> flowSensorI;
                case CONFIG, PWM, ANALOG_EXTORT -> null;
                case ANALOG -> {
                    AnalogSensor aSensor = (AnalogSensor) sensor;
                    yield new FlowSensorAnalogI(flowSensorI).setPin(aSensor.getPin()).setValue(aSensor.getAdcRaw());
                }
            };
            if (fT != null) {
                flowApi.takeBroadcaster(null).write(objectOutputStream -> {
                    try {
                        objectOutputStream.writeUnshared(fT);
                    } catch (IOException e) {
                        throw new SensorApiException(e);
                    }
                });
            }
        }
    }
}