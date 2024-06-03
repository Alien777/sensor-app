package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.bus.FlowSensorIInputStreamBus;
import pl.lasota.sensor.flow.model.FlowSensorAnalogI;
import pl.lasota.sensor.flow.model.FlowSensorI;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.StartFlowNode;
import pl.lasota.sensor.flow.services.nodes.utils.*;

import java.util.function.BiConsumer;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@FlowNode
@Slf4j
public class ListeningSensorNode extends Node implements StartFlowNode {

    private final Data data;
    private final FlowSensorIInputStreamBus slm;
    private final BiConsumer<String, FlowSensorI> consumer;

    private ListeningSensorNode(String id, GlobalContext globalContext, Data data, FlowSensorIInputStreamBus slm) {
        super(id, globalContext);
        this.data = data;
        this.slm = slm;
        consumer = (s, sensor) -> {
            try {
                if (globalContext.isRunningRightNow.get()) {
                    return;
                }
                globalContext.isRunningRightNow.set(true);

                if (globalContext.isStopped()) {
                    return;
                }
                if (!globalContext.getMember().getId().equals(sensor.getMemberId())) {
                    return;
                }
                if (!data.getDeviceId().equals(sensor.getDeviceId())) {
                    return;
                }
                LocalContext localContext = new LocalContext();
                if (!analyze(sensor, localContext)) {
                    return;
                }

                ListeningSensorNode.super.execute(localContext);
                globalContext.isRunningRightNow.set(false);

            } catch (Exception e) {
                log.error("Occurred flow exception ", e);
            }
        };
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        String type = fString(node, "messageType").toUpperCase();
        ListeningSensorNode.Data data = ListeningSensorNode.Data.create(deviceId, type);
        return new ListeningSensorNode(ref, globalContext, data, context.getBean(FlowSensorIInputStreamBus.class));
    }


    @Override
    public boolean start() {
        try {
            slm.addConsumer(consumer);
        } catch (Exception e) {
            log.error("Problem with start node ", e);
            return false;
        }
        return true;
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
        slm.removeConsumer(consumer);
        super.clear();
    }


    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final String forMessageType;
    }
}
