package pl.lasota.sensor.flows.nodes;

import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

public class AsyncNode extends Node {

    @Override
    public void execute(PrivateContext privateContext) {
        Thread thread = new Thread(() -> {
            try {
                super.execute(privateContext);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                privateContext.getThreads().remove(id);
            }
        });
        privateContext.getThreads().put(id, thread);
        thread.start();
    }
}
