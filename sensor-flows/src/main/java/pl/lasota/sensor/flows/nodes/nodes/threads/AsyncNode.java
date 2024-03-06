package pl.lasota.sensor.flows.nodes.nodes.threads;

import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.flows.nodes.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

@Slf4j
@FlowNode
public class AsyncNode extends Node {

    public AsyncNode(PrivateContext privateContext) {
        super(privateContext);
    }

    @Override
    public void execute() {
        Thread thread = new Thread(() -> {
            try {
                super.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                privateContext.getThreads().remove(id);
            }
        });
        privateContext.getThreads().put(id, thread);
        thread.start();
    }

    @Override
    public void clear() {
        Thread remove = privateContext.getThreads().remove(id);
        if (remove != null) {
            remove.interrupt();
        }
        super.clear();
    }

}
