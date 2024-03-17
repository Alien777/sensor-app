package pl.lasota.sensor.flows.nodes.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.core.exceptions.FlowException;
import pl.lasota.sensor.core.exceptions.FlowRuntimeException;
import pl.lasota.sensor.core.models.mqtt.payload.MessageType;
import pl.lasota.sensor.core.models.sensor.ConnectedDevice;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.models.sensor.SingleAdcSignal;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.StartFlowNode;
import pl.lasota.sensor.flows.nodes.utils.*;

import java.util.concurrent.atomic.AtomicBoolean;

@FlowNode
@Slf4j
public class ListeningSensorNode extends Node implements StartFlowNode, SensorListening {
    private final SensorListeningManager slm;
    private final KeySensor keySensor;
    private final Data data;


    public ListeningSensorNode(String id, GlobalContext globalContext, Data data, SensorListeningManager slm) {
        super(id, globalContext);
        this.data = data;
        keySensor = new KeySensor(data.deviceId, id);
        this.slm = slm;
    }

    @Override
    public boolean start() {
        try {
            boolean fromEsp = MessageType.isFromDevice(data.forMessageType);
            if (!fromEsp) {
                return false;
            }
            slm.addClient(keySensor, this);
        } catch (Exception e) {
            log.error("Problem with start node ", e);
            return false;
        }
        return true;
    }

    @Override
    public void onReceiving(Sensor sensor) {
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


    private boolean analyze(Sensor sensor, LocalContext localContext) {
        if (!sensor.getMessageType().equals(data.forMessageType)) {
            return false;
        }

        return switch (sensor.getMessageType()) {
            case DEVICE_CONNECTED -> {
                ConnectedDevice s = (ConnectedDevice) sensor;
                yield true;
            }
            case SINGLE_ADC_SIGNAL -> {
                SingleAdcSignal s = (SingleAdcSignal) sensor;
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
        private String deviceId;
        private MessageType forMessageType;
    }
}
