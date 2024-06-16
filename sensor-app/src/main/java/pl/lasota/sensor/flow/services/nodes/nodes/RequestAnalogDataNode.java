package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.device.model.SendForAnalogDataI;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fInteger;
import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@FlowNode
public class RequestAnalogDataNode extends Node {

    private final Data data;
    private final DeviceSendMessageInterface deviceSendMessageInterface;

    private RequestAnalogDataNode(String id, GlobalContext globalContext, Data data, DeviceSendMessageInterface deviceSendMessageInterface) {
        super(id, globalContext);
        this.data = data;
        this.deviceSendMessageInterface = deviceSendMessageInterface;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        Integer pin = fInteger(node, "pin");
        return new RequestAnalogDataNode(ref, globalContext, RequestAnalogDataNode.Data.create(deviceId, pin), context.getBean(DeviceSendMessageInterface.class));
    }

    @Override
    public void execute(LocalContext localContext) throws Exception {
        deviceSendMessageInterface.sendRequestForDataAnalog(new SendForAnalogDataI(data.deviceId, data.pin));
        super.execute(localContext);
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final int pin;
    }
}
