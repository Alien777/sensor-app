package pl.lasota.sensor.flows.nodes.nodes;

import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

@Slf4j
@FlowNode
public class AsyncNode extends Node {

    public AsyncNode(String id, GlobalContext globalContext) {
        super(id, globalContext);
    }

    @Override
    public void execute(LocalContext localContext) {
        Thread thread = new Thread(() -> {
            try {
                LocalContext newLocalContext = new LocalContext();
                super.execute(newLocalContext);
            } finally {
                globalContext.getThreads().remove(id);
            }
        });
        globalContext.getThreads().put(id, thread);
        thread.start();
    }

    @Override
    public void clear() {
        Thread remove = globalContext.getThreads().remove(id);
        if (remove != null) {
            remove.interrupt();
        }
        super.clear();
    }

}
