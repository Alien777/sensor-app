package pl.lasota.sensor.flows.nodes.nodes;

import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

import java.time.Duration;


@FlowNode
public class WaitNode extends Node {

    private final String waitForThread;
    private final long waitTimeSecond;

    public WaitNode(String id, GlobalContext globalContext, String waitForThread, long waitTimeSecond) {
        super(id, globalContext);
        this.waitForThread = waitForThread;
        this.waitTimeSecond = waitTimeSecond;
    }

    @Override
    public void execute(LocalContext localContext) throws Exception {
        Thread thread = globalContext.getThreads().get(waitForThread);
        if (thread != null) {
            thread.join(Duration.ofSeconds(waitTimeSecond));
        }
        super.execute(localContext);
    }

}
