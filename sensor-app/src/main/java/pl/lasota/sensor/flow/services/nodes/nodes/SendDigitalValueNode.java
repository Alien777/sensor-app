package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.device.DeviceConfigInterface;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.device.model.SendDigitalI;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;
import pl.lasota.sensor.flow.services.nodes.utils.NodeUtils;

import java.util.List;
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
        Integer pin = fInteger(node, "pin");
        String valueKey = fString(node, "valueVariable");
        Data data = Data.create(deviceId, valueKey, pin);
        DeviceSendMessageInterface dsmi = context.getBean(DeviceSendMessageInterface.class);
        DeviceConfigInterface dci = context.getBean(DeviceConfigInterface.class);
        List<Integer> configDigitalPins = dci.getConfigDigitalPins(data.getDeviceId());
        if (!configDigitalPins.contains(data.getPin())) {
            throw new SensorFlowException("Digital pin {} not found for device {}", pin, deviceId);
        }
        return new SendDigitalValueNode(ref, globalContext, data, dsmi);
    }

    @Override
    public void execute(LocalContext localContext) throws Exception {
        Optional<Integer> value = NodeUtils.getValue(data.valueVariable, localContext, flowContext, globalContext, Integer.class);
        if (value.isPresent()) {
            deviceApiInterface.sendDigitalValueToDevice(new SendDigitalI(data.deviceId, data.pin, value.get()));
            super.execute(localContext);
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
