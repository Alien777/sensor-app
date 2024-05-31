package pl.lasota.sensor.flows.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.StartFlowNode;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

@Slf4j
@FlowNode
public class FireOnceNode  extends Node implements StartFlowNode {

    public FireOnceNode(String id, GlobalContext globalContext) {
        super(id, globalContext);
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        return new FireOnceNode(ref, globalContext);
    }

    @Override
    public void execute(LocalContext localContext) {
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public boolean start() {
        LocalContext localContext = new LocalContext();
        super.execute(localContext);
        return true;
    }
}
