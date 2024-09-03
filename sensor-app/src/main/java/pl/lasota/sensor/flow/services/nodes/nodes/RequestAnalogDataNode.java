package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.device.model.AnalogReadOneShotRequestMessage;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.services.keeper.KeeperForSetUp;
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
        Integer gpio = fInteger(node, "gpio");
        KeeperForSetUp dci = context.getBean(KeeperForSetUp.class);

        if (!dci.contains(deviceId, KeeperForSetUp.TypeConfig.ANALOG, gpio)) {
            throw new SensorFlowException("Analog pin {} not found for device {}", gpio, deviceId);
        }
        return new RequestAnalogDataNode(ref, globalContext, RequestAnalogDataNode.Data.create(deviceId, gpio), context.getBean(DeviceSendMessageInterface.class));
    }

    @Override
    protected void fireChildNodes(LocalContext localContext) throws Exception {
        deviceSendMessageInterface.sendAnalogReadOneShotRequest(new AnalogReadOneShotRequestMessage(data.deviceId, data.pin));
        super.fireChildNodes(localContext);
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final int pin;
    }
}
