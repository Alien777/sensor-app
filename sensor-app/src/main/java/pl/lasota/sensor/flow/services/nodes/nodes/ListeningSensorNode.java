package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.bus.FlowSensorIInputStreamBus;
import pl.lasota.sensor.flow.model.FlowSensorAnalogI;
import pl.lasota.sensor.flow.model.FlowSensorI;
import pl.lasota.sensor.flow.services.nodes.AsyncNodeConsumer;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.NodeStart;
import pl.lasota.sensor.flow.services.nodes.utils.FlowContext;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@FlowNode
@Slf4j
public class ListeningSensorNode extends NodeStart implements AsyncNodeConsumer<String, FlowSensorI> {

    private final Data data;
    private final FlowSensorIInputStreamBus slm;

    private ListeningSensorNode(String id, GlobalContext globalContext, Data data, FlowSensorIInputStreamBus slm) {
        super(id, globalContext);
        this.data = data;
        this.slm = slm;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        String type = fString(node, "messageType").toUpperCase();
        ListeningSensorNode.Data data = ListeningSensorNode.Data.create(deviceId, type);
        return new ListeningSensorNode(ref, globalContext, data, context.getBean(FlowSensorIInputStreamBus.class));
    }

    @Override
    public void config(FlowContext flowContext) throws Exception {
        super.propagateFlowContext(flowContext);
        slm.addConsumer(this);
    }

    @Override
    public void clear() {
        slm.removeConsumer(this);
        super.clear();
    }


    @Override
    public boolean preConsume(String s, FlowSensorI sensor) {
        if (!flowContext.getMember().getId().equals(sensor.getMemberId())) {
            return false;
        }
        if (!data.getDeviceId().equals(sensor.getDeviceId())) {
            return false;
        }
        return true;
    }

    @Override
    public void consume(String s, FlowSensorI flowSensorI) throws Exception {
        LocalContext localContext = new LocalContext();
        if (!analyze(flowSensorI, localContext)) {
            return;
        }
        fireChildNodes(localContext);
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final String forMessageType;
    }


    private boolean analyze(FlowSensorI sensor, LocalContext localContext) {
        if (!sensor.getMessageType().equals(data.forMessageType)) {
            return false;
        }

        return switch (sensor.getMessageType()) {
            case "DEVICE_CONNECTED", "PING_ACK", "PWM_ACK" -> {
                yield true;
            }
            case "ANALOG" -> {
                FlowSensorAnalogI s = (FlowSensorAnalogI) sensor;
                localContext.getVariables().put(id, s);
                yield true;
            }
            default -> false;
        };
    }
}
