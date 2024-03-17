package pl.lasota.sensor.flows;

import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

public class NodeMock extends Node {
    public NodeMock(GlobalContext globalContext) {
        super("mockId",globalContext);
    }

    @Override
    public void execute(LocalContext localContext)  {

    }

    @Override
    public void clear() {

    }
}
