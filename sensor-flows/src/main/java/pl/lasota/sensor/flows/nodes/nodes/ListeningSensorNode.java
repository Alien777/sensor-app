package pl.lasota.sensor.flows.nodes.nodes;

import pl.lasota.sensor.flows.nodes.utils.KeySensor;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;

@FlowNode
public class ListeningSensorNode extends Node {

    private final SensorListeningManager slm;
    private final KeySensor keySensor;


    public ListeningSensorNode(PrivateContext privateContext, String deviceId, SensorListeningManager slm) {
        super(privateContext);
        this.slm = slm;
        keySensor = new KeySensor(deviceId, id);
        slm.addClient(keySensor, sensor -> super.execute());
    }

    @Override
    public void execute() throws Exception {

    }

    @Override
    public void clear() {
        slm.removeClient(keySensor);
        super.clear();
    }
}
