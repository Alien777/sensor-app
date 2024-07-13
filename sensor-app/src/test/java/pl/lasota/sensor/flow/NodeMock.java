package pl.lasota.sensor.flow;

import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

public class NodeMock extends Node {
    public NodeMock(GlobalContext globalContext) {
        super("mockId",globalContext);
    }

    @Override
    public void fireChildNodes(LocalContext localContext)  {

    }

    @Override
    public void clear() {

    }
}
