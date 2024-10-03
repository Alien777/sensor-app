package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.bus.FlowSensorIInputStreamBus;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.device.model.AnalogReadOneShotRequestMessage;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.FlowApiInterface;
import pl.lasota.sensor.flow.services.keeper.KeeperForSetUp;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.send.SendAndWait;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fInteger;
import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@FlowNode
public class RequestAnalogDataNode extends Node {

    private final Data data;
    private final DeviceSendMessageInterface deviceSendMessageInterface;
    private final FlowSensorIInputStreamBus flowSensorIInputStreamBus;
    private final FlowApiInterface flowApiInterface;

    private RequestAnalogDataNode(String id, GlobalContext globalContext, Data data, DeviceSendMessageInterface deviceSendMessageInterface,
                                  FlowSensorIInputStreamBus flowSensorIInputStreamBus,
                                  FlowApiInterface flowApiInterface) {
        super(id, globalContext);
        this.data = data;
        this.deviceSendMessageInterface = deviceSendMessageInterface;
        this.flowSensorIInputStreamBus = flowSensorIInputStreamBus;
        this.flowApiInterface = flowApiInterface;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        Integer gpio = fInteger(node, "gpio");
        KeeperForSetUp dci = context.getBean(KeeperForSetUp.class);

        if (!dci.contains(deviceId, KeeperForSetUp.TypeConfig.ANALOG, gpio)) {
            throw new SensorFlowException("Analog pin {} not found for device {}", gpio, deviceId);
        }

        FlowSensorIInputStreamBus flowSensorIInputStreamBus = context.getBean(FlowSensorIInputStreamBus.class);
        FlowApiInterface flowApiInterface1 = context.getBean(FlowApiInterface.class);
        return new RequestAnalogDataNode(ref, globalContext, RequestAnalogDataNode.Data.create(deviceId, gpio),
                context.getBean(DeviceSendMessageInterface.class), flowSensorIInputStreamBus, flowApiInterface1);
    }

    @Override
    protected void fireChildNodes(LocalContext localContext) throws Exception {
        boolean send = SendAndWait.
                of(flowSensorIInputStreamBus, () -> deviceSendMessageInterface.sendAnalogReadOneShotRequest(new AnalogReadOneShotRequestMessage(data.deviceId, data.pin)))
                .send();
        if (send) {
            super.fireChildNodes(localContext);
        } else {
            flowApiInterface.restartFlow(globalContext.getFlowId());
        }
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final int pin;
    }
}
