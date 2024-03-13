package pl.lasota.sensor.flows.nodes.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.lasota.sensor.core.models.mqtt.payload.MessageType;
import pl.lasota.sensor.core.models.rest.SendPwmS;
import pl.lasota.sensor.core.restapi.SensorApiEndpoint;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;
import pl.lasota.sensor.flows.nodes.utils.NodeUtils;

import java.util.Optional;

@FlowNode
public class SendPwmValueNode extends Node {

    private final Data data;
    private final SensorApiEndpoint sensorApiEndpoint;

    public SendPwmValueNode(String id, GlobalContext globalContext, Data data, SensorApiEndpoint sensorApiEndpoint) {
        super(id, globalContext);
        this.data = data;
        this.sensorApiEndpoint = sensorApiEndpoint;
    }

    @Override
    public void execute(LocalContext localContext) throws Exception {
        Optional<Long> value = NodeUtils.getValue(data.valueVariable, localContext, globalContext);
        if (value.isPresent()) {
            sensorApiEndpoint.sendPwmValueToDevice(new SendPwmS(data.memberKey, data.deviceId, data.pin, value.get()));
            super.execute(localContext);
        }
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final String memberKey;
        private final String valueVariable;
        private final int pin;
    }
}
