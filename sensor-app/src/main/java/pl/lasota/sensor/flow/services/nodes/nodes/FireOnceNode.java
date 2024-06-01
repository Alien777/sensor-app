package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.StartFlowNode;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

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
