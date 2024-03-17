package pl.lasota.sensor.flows.nodes.nodes;

import pl.lasota.sensor.core.exceptions.FlowRuntimeException;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

import java.time.Duration;

@FlowNode
public class SleepNode extends Node {
    private final long sleepTimeSeconds;

    public SleepNode(String id, GlobalContext globalContext, long sleepTimeSeconds) {
        super(id, globalContext);
        this.sleepTimeSeconds = sleepTimeSeconds;
    }

    @Override
    public void execute(LocalContext localContext) {
        try {
            Thread.sleep(Duration.ofSeconds(sleepTimeSeconds));
            super.execute(localContext);
        } catch (InterruptedException e) {
            throw new FlowRuntimeException(e);
        }
    }

}
