package pl.lasota.sensor.flows.nodes.nodes.threads;

import pl.lasota.sensor.flows.nodes.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

import java.time.Duration;

@FlowNode
public class SleepNode extends Node {
    private final long sleepTimeSeconds;

    public SleepNode(PrivateContext privateContext,long sleepTimeSeconds) {
        super(privateContext);
        this.sleepTimeSeconds = sleepTimeSeconds;
    }

    @Override
    public void execute() throws Exception {
        Thread.sleep(Duration.ofSeconds(sleepTimeSeconds));
        super.execute();
    }

}
