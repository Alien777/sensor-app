package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.bus.WaitForResponseInputStreamBus;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.device.model.PwmWriteRequestMessage;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.services.keeper.KeeperForSetUp;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.send.SendAndWait;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;
import pl.lasota.sensor.flow.services.nodes.utils.NodeUtils;

import java.util.Optional;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fInteger;
import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@FlowNode
public class SendPwmValueNode extends Node {

    private static final Logger log = LoggerFactory.getLogger(SendPwmValueNode.class);
    private final Data data;
    private final DeviceSendMessageInterface deviceSendMessageInterface;
    private final WaitForResponseInputStreamBus waitForResponseInputStreamBus;

    private SendPwmValueNode(String id, GlobalContext globalContext, Data data, DeviceSendMessageInterface deviceSendMessageInterface, WaitForResponseInputStreamBus waitForResponseInputStreamBus) {
        super(id, globalContext);
        this.data = data;
        this.deviceSendMessageInterface = deviceSendMessageInterface;
        this.waitForResponseInputStreamBus = waitForResponseInputStreamBus;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        Integer gpio = fInteger(node, "gpio");
        String valueKey = fString(node, "valueVariable");
        String durationKey = fString(node, "durationVariable");
        Data data = Data.create(deviceId, valueKey, durationKey, gpio);
        DeviceSendMessageInterface dsmi = context.getBean(DeviceSendMessageInterface.class);
        KeeperForSetUp dci = context.getBean(KeeperForSetUp.class);

        if (!dci.contains(deviceId, KeeperForSetUp.TypeConfig.PWM, gpio)) {
            throw new SensorFlowException("Pwm pin {} not found for device {}", gpio, deviceId);
        }

        return new SendPwmValueNode(ref, globalContext, data, dsmi, context.getBean(WaitForResponseInputStreamBus.class));
    }

    @Override
    protected void fireChildNodes(LocalContext localContext) throws Exception {
        Optional<Long> value = NodeUtils.getValue(data.valueVariable, localContext, flowContext, globalContext, Long.class);
        Optional<Long> duration = NodeUtils.getValue(data.durationVariable, localContext, flowContext, globalContext, Long.class);

        if (value.isPresent()) {
            boolean send = SendAndWait.
                    of(waitForResponseInputStreamBus, () -> deviceSendMessageInterface.sendPwmWriteRequest(new PwmWriteRequestMessage(data.deviceId, data.pin, value.get(), duration.orElse(0L))))
                    .send();
            if (send) {
                super.fireChildNodes(localContext);
            }
        }
    }

    @Override
    public void clear() {
        try {
            deviceSendMessageInterface.sendPwmWriteRequest(new PwmWriteRequestMessage(data.deviceId, data.pin, 0, 0));
        } catch (Exception e) {
            log.info("Problem to send value pwm during clear", e);
        }
        super.clear();
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
