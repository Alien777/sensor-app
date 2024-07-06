package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.device.DeviceConfigInterface;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.device.model.SendPwmI;
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
public class SendPwmValueNode extends Node {

    private final Data data;
    private final DeviceSendMessageInterface deviceSendMessageInterface;

    private SendPwmValueNode(String id, GlobalContext globalContext, Data data, DeviceSendMessageInterface deviceSendMessageInterface) {
        super(id, globalContext);
        this.data = data;
        this.deviceSendMessageInterface = deviceSendMessageInterface;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        Integer pin = fInteger(node, "pin");
        String valueKey = fString(node, "valueVariable");
        String durationKey = fString(node, "durationVariable");
        Data data = Data.create(deviceId, valueKey, durationKey, pin);
        DeviceSendMessageInterface dsmi = context.getBean(DeviceSendMessageInterface.class);
        DeviceConfigInterface dci = context.getBean(DeviceConfigInterface.class);
        List<Integer> configPwmPins = dci.getConfigPwmPins(data.getDeviceId());
        if (!configPwmPins.contains(data.getPin())) {
            throw new SensorFlowException("Pwm pin {} not found for device {}", pin, deviceId);
        }
        return new SendPwmValueNode(ref, globalContext, data, dsmi);
    }

    @Override
    public void execute(LocalContext localContext) throws Exception {
        Optional<Long> value = NodeUtils.getValue(data.valueVariable, localContext, flowContext, globalContext, Long.class);
        Optional<Long> duration = NodeUtils.getValue(data.durationVariable, localContext, flowContext, globalContext, Long.class);
        if (value.isPresent()) {
            deviceSendMessageInterface.sendPwmValueToDevice(new SendPwmI(data.deviceId, data.pin, value.get(),duration.orElse(0L)));
            super.execute(localContext);
        }
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final String valueVariable;
        private final String durationVariable;
        private final int pin;
    }
}
