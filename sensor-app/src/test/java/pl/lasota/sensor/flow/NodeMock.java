package pl.lasota.sensor.flow;

import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

public class NodeMock extends Node {
    private final Runnable runnable;

    public NodeMock(GlobalContext globalContext, Runnable runnable) {
        super("mockId", globalContext);
        this.runnable = runnable;
    }

    @Override
    public void fireChildNodes(LocalContext localContext) {
        runnable.run();
    }


    @Override
    public void clear() {

    }
}
