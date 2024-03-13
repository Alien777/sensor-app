package pl.lasota.sensor.flows.nodes.nodes.start;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.lasota.sensor.core.exceptions.FlowException;
import pl.lasota.sensor.core.models.mqtt.payload.MessageType;
import pl.lasota.sensor.core.models.sensor.ConnectedDevice;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.models.sensor.SingleAdcSignal;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.KeySensor;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;

@FlowNode
public class ListeningSensorNode extends Node {


    private final SensorListeningManager slm;
    private final KeySensor keySensor;
    private final Data data;


    public ListeningSensorNode(String id, GlobalContext globalContext, Data data, SensorListeningManager slm) throws FlowException {
        super(id, globalContext);
        this.data = data;
        boolean fromEsp = MessageType.isFromEsp(data.forMessageType);
        if (!fromEsp) {
            throw new FlowException("Message is not correct type");
        }
        this.slm = slm;
        keySensor = new KeySensor(data.deviceId, id);
        slm.addClient(keySensor, sensor -> {

            boolean fromEspCheck = MessageType.isFromEsp(data.forMessageType);
            if (!fromEspCheck) {
                throw new FlowException("Message is not correct type");
            }

            LocalContext localContext = new LocalContext();
            if (!analyze(sensor, localContext)) {
                return;
            }
            super.execute(localContext);
        });
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
                localContext.getVariables().put(id, s.getAdcRaw());
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public void execute(LocalContext localContext) throws Exception {

    }

    @Override
    public void clear() {
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
