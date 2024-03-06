package pl.lasota.sensor.flows.nodes.nodes.threads;

import pl.lasota.sensor.flows.nodes.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

import java.time.Duration;


@FlowNode
public class SleepNode extends Node {
    private final long sleepTimeSecond;

    public SleepNode(PrivateContext privateContext,long sleepTimeSecond) {
        super(privateContext);
        this.sleepTimeSecond = sleepTimeSecond;
    }

    @Override
    public void execute() throws Exception {
        Thread.sleep(Duration.ofSeconds(sleepTimeSecond));
        super.execute();
    }

}
