package pl.lasota.sensor.flows.nodes.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.lasota.sensor.core.apis.SensorMicroserviceEndpoint;
import pl.lasota.sensor.core.apis.model.sensor.SendPwm;
import pl.lasota.sensor.core.exceptions.FlowRuntimeException;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;
import pl.lasota.sensor.flows.nodes.utils.NodeUtils;

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
                sensorMicroserviceEndpoint.sendPwmValueToDevice(new SendPwm(data.deviceId, data.token, data.pin, value.get()));
                super.execute(localContext);
            } catch (Exception e) {
                throw new FlowRuntimeException(e);
            }
        }
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private String memberId;
        private final String deviceId;
        private String token;
        private final String valueVariable;
        private final int pin;

        public void setUp(String memberId, String token) {
            this.memberId = memberId;
            this.token = token;
        }
    }
}
