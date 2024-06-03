package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

@Slf4j
@FlowNode
public class AsyncNode extends Node {

    private AsyncNode(String id, GlobalContext globalContext) {
        super(id, globalContext);
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        return new AsyncNode(ref, globalContext);
    }

    @Override
    public void execute(LocalContext localContext) {
        Thread thread = new Thread(() -> {
            try {
                try {
                    super.execute(new LocalContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } finally {
                flowContext.getThreads().remove(id);
            }
        });
        flowContext.getThreads().put(id, thread);
        thread.start();
    }

    @Override
    public void clear() {
        super.clear();
        Thread remove = flowContext.getThreads().remove(id);
        if (remove != null) {
            remove.interrupt();
        }
    }

}
