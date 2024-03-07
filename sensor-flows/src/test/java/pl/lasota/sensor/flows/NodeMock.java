package pl.lasota.sensor.flows;

import pl.lasota.sensor.flows.nodes.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

public class NodeMock extends Node {
    public NodeMock(PrivateContext privateContext) {
        super(privateContext);
    }

    @Override
    public void execute() throws Exception {

    }

    @Override
    public void clear() {

    }
}
