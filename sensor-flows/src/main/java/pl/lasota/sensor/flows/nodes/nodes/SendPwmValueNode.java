package pl.lasota.sensor.flows.nodes.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.lasota.sensor.flows.exceptions.SensorFlowException;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;
import pl.lasota.sensor.flows.nodes.utils.NodeUtils;
import pl.lasota.sensor.internal.apis.api.SensorMicroserviceEndpoint;
import pl.lasota.sensor.internal.apis.api.device.SendPwmI;

import java.util.Optional;

@FlowNode

public class SendPwmValueNode extends Node {

    private final Data data;
    private final SensorMicroserviceEndpoint sensorMicroserviceEndpoint;

    public SendPwmValueNode(String id, GlobalContext globalContext, Data data, SensorMicroserviceEndpoint sensorMicroserviceEndpoint) {
        super(id, globalContext);
        this.data = data;
        this.sensorMicroserviceEndpoint = sensorMicroserviceEndpoint;
    }

    @Override
    public void execute(LocalContext localContext) {
        Optional<Long> value = NodeUtils.getValue(data.valueVariable, localContext, globalContext, Long.class);
        if (value.isPresent()) {
            try {
                sensorMicroserviceEndpoint.sendPwmValueToDevice(new SendPwmI(data.deviceId, data.pin, value.get()));
                super.execute(localContext);
            } catch (Exception e) {
                throw new SensorFlowException("Occurred problem with send pwm value to device", e);
            }
        }
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final String valueVariable;
        private final int pin;
    }
}
