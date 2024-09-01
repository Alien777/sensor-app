package pl.lasota.sensor.flow.services.nodes;

import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.flow.services.nodes.utils.FlowContext;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j

public abstract class NodeStart extends Node {

    private final ReentrantLock lock = new ReentrantLock();

    public NodeStart(String id, GlobalContext globalContext) {
        super(id, globalContext);
    }

    public abstract void config(FlowContext flowContext) throws Exception;

    public void initiate(FlowContext flowContext) throws Exception {
        config(flowContext);
    }

    @Override
    public void fireChildNodes(LocalContext localContext) {
        try {
            run(localContext);
        } catch (Exception e) {
            log.error("Problem execute node {}", this.getClass().getName(), e);
        }
    }

    private void run(LocalContext localContext) throws Exception {
        boolean acquired = false;
        try {
            acquired = lock.tryLock(10, TimeUnit.MILLISECONDS);
            if (acquired) {
                super.fireChildNodes(localContext);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }
}
