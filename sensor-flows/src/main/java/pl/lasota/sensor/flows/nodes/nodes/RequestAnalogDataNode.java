package pl.lasota.sensor.flows.nodes.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.lasota.sensor.core.apis.SensorMicroserviceEndpoint;
import pl.lasota.sensor.core.apis.model.sensor.SendForAnalogData;
import pl.lasota.sensor.core.exceptions.FlowRuntimeException;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

@FlowNode
public class RequestAnalogDataNode extends Node {

    private final Data data;
    private final SensorMicroserviceEndpoint sensorMicroserviceEndpoint;

    public RequestAnalogDataNode(String id, GlobalContext globalContext, Data data, SensorMicroserviceEndpoint sensorMicroserviceEndpoint) {
        super(id, globalContext);
        this.data = data;
        this.sensorMicroserviceEndpoint = sensorMicroserviceEndpoint;
    }

    @Override
    public void execute(LocalContext localContext) {
        try {
            sensorMicroserviceEndpoint.sendRequestForDataAnalog(new SendForAnalogData(data.deviceId, data.pin));
            super.execute(localContext);
        } catch (Exception e) {
            throw new FlowRuntimeException(e);
        }
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final int pin;
    }
}
