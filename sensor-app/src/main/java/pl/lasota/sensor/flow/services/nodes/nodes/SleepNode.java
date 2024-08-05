package pl.lasota.sensor.flow.services.nodes.nodes;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import java.time.Duration;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fLong;

@FlowNode
public class SleepNode extends Node {
    private final long sleepTime;

    private SleepNode(String id, GlobalContext globalContext, long sleepTime) {
        super(id, globalContext);
        this.sleepTime = sleepTime;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        Long sleepTime = fLong(node, "sleepTime");
        return new SleepNode(ref, globalContext, sleepTime);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    protected void fireChildNodes(LocalContext localContext) throws Exception {
        Thread.sleep(Duration.ofMillis(sleepTime));
        super.fireChildNodes(localContext);
    }

}
