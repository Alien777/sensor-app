package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.flow.model.FlowSensorAnalogI;
import pl.lasota.sensor.flow.model.FlowSensorI;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.StartFlowNode;
import pl.lasota.sensor.flow.services.nodes.utils.*;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@FlowNode
@Slf4j
public class ListeningSensorNode extends Node implements StartFlowNode, SensorListening {
    private final SensorListeningManager slm;
    private final KeySensor keySensor;
    private final Data data;

    private ListeningSensorNode(String id, GlobalContext globalContext, Data data, SensorListeningManager slm) {
        super(id, globalContext);
        this.data = data;
        keySensor = new KeySensor(data.deviceId, id);
        this.slm = slm;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        String type = fString(node, "messageType").toUpperCase();
        ListeningSensorNode.Data data = ListeningSensorNode.Data.create(deviceId, type);
        return new ListeningSensorNode(ref, globalContext, data, context.getBean(SensorListeningManager.class));
    }

    @Override
    public boolean start() {
        try {
            slm.addClient(keySensor, this);
        } catch (Exception e) {
            log.error("Problem with start node ", e);
            return false;
        }
        return true;
    }

    @Override
    public void onReceiving(FlowSensorI sensor) {
        try {
            if (globalContext.isStopped()) {
                return;
            }
            LocalContext localContext = new LocalContext();
            if (!analyze(sensor, localContext)) {
                return;
            }
            super.execute(localContext);

        } catch (Exception e) {
            log.error("Occurred flow exception ", e);
        }
    }


    private boolean analyze(FlowSensorI sensor, LocalContext localContext) {
        if (!sensor.getMessageType().equals(data.forMessageType)) {
            return false;
        }

        return switch (sensor.getMessageType()) {
            case "DEVICE_CONNECTED" -> {
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


    @Override
    public void execute(LocalContext localContext) {
        throw new UnsupportedOperationException("Please execute start instead execute");
    }

    @Override
    public void clear() {
        globalContext.stopFlow();
        slm.removeClient(keySensor);
        super.clear();
    }


    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final String forMessageType;
    }
}
