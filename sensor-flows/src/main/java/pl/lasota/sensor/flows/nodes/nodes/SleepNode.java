package pl.lasota.sensor.flows.nodes.nodes;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.flows.exceptions.SensorFlowException;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

import java.time.Duration;

import static pl.lasota.sensor.flows.nodes.builder.ParserFlows.fLong;

@FlowNode
public class SleepNode extends Node {
    private final long sleepTimeSeconds;

    private SleepNode(String id, GlobalContext globalContext, long sleepTimeSeconds) {
        super(id, globalContext);
        this.sleepTimeSeconds = sleepTimeSeconds;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        Long sleepTimeSeconds = fLong(node, "sleepTimeSeconds");
        return new SleepNode(ref, globalContext, sleepTimeSeconds);
    }

    @Override
    public void execute(LocalContext localContext) {
        try {
            Thread.sleep(Duration.ofSeconds(sleepTimeSeconds));
            super.execute(localContext);
        } catch (InterruptedException e) {
            throw new SensorFlowException("Occurred problem with interrupt", e);
        }
    }

}
