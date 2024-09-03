package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.device.model.DigitalWriteMessage;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.services.keeper.KeeperForSetUp;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;
import pl.lasota.sensor.flow.services.nodes.utils.NodeUtils;

import java.util.Optional;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fInteger;
import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@FlowNode
public class SendDigitalValueNode extends Node {

    private final Data data;
    private final DeviceSendMessageInterface deviceApiInterface;

    private SendDigitalValueNode(String id, GlobalContext globalContext, Data data, DeviceSendMessageInterface deviceApiInterface) {
        super(id, globalContext);
        this.data = data;
        this.deviceApiInterface = deviceApiInterface;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        Integer gpio = fInteger(node, "gpio");
        String valueKey = fString(node, "valueVariable");
        Data data = Data.create(deviceId, valueKey, gpio);
        DeviceSendMessageInterface dsmi = context.getBean(DeviceSendMessageInterface.class);
        KeeperForSetUp dci = context.getBean(KeeperForSetUp.class);

        if (!dci.contains(deviceId, KeeperForSetUp.TypeConfig.DIGITAL, gpio)) {
            throw new SensorFlowException("Digital pin {} not found for device {}", gpio, deviceId);
        }
        return new SendDigitalValueNode(ref, globalContext, data, dsmi);
    }

    @Override
    protected void fireChildNodes(LocalContext localContext) throws Exception {
        Optional<Boolean> value = NodeUtils.getValue(data.valueVariable, localContext, flowContext, globalContext, Boolean.class);
        if (value.isPresent()) {
            deviceApiInterface.sendDigitalWriteRequest(new DigitalWriteMessage(data.deviceId, data.gpio, value.get()));
            super.fireChildNodes(localContext);
        }
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final String valueVariable;
        private final int gpio;
    }
}
