package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.device.DeviceApiInterface;
import pl.lasota.sensor.device.model.SendForAnalogDataI;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fInteger;
import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@FlowNode
public class RequestAnalogDataNode extends Node {

    private final Data data;
    private final DeviceApiInterface deviceApiInterface;

    private RequestAnalogDataNode(String id, GlobalContext globalContext, Data data,  DeviceApiInterface deviceApiInterface) {
        super(id, globalContext);
        this.data = data;
        this.deviceApiInterface = deviceApiInterface;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        Integer pin = fInteger(node, "pin");
        return new RequestAnalogDataNode(ref, globalContext, RequestAnalogDataNode.Data.create(deviceId, pin), context.getBean(DeviceApiInterface.class));
    }

    @Override
    public void execute(LocalContext localContext) {
        try {
            deviceApiInterface.sendRequestForDataAnalog(new SendForAnalogDataI(data.deviceId, data.pin));
            super.execute(localContext);
        } catch (Exception e) {
            throw new SensorFlowException("Occurred problem with send request to analog data", e);
        }
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final int pin;
    }
}
