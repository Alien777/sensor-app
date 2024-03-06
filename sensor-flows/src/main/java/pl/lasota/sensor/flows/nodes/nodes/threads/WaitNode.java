package pl.lasota.sensor.flows.nodes.nodes.threads;

import pl.lasota.sensor.flows.nodes.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

import java.time.Duration;


@FlowNode
public class WaitNode extends Node {

    private final String waitForThread;
    private final long waitTimeSecond;

    public WaitNode(PrivateContext privateContext, String waitForThread, long waitTimeSecond) {
        super(privateContext);
        this.waitForThread = waitForThread;
        this.waitTimeSecond = waitTimeSecond;
    }

    @Override
    public void execute() throws Exception {
        Thread thread = privateContext.getThreads().get(waitForThread);
        if (thread != null) {
            thread.join(Duration.ofSeconds(waitTimeSecond));
        }
        super.execute();
    }

}
