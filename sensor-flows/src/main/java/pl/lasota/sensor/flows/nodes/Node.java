package pl.lasota.sensor.flows.nodes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public abstract class Node {
    @Getter
    private final List<Node> nodes = new ArrayList<>();

    protected final String id;

    protected final GlobalContext globalContext;

    public void execute(LocalContext localContext) throws Exception {
        for (Node node : nodes) {
            if (Thread.currentThread().isInterrupted()) {
                log.info("Thread was interrupted, stopping execution.");
                return;
            }
            log.info("Execute node {} from {} to {}", id, this.getClass().getName(), node.getClass().getName());
            node.execute(localContext);
        }
    }

    public void clear() {
        for (Node node : nodes) {
            try {
                node.clear();
            } catch (Exception e) {
                log.error("Problem with clear component {}", id);
            }
        }
    }

    public void add(Node node) {
        nodes.add(node);
    }

    public void addFirst(Node node) {
        nodes.addFirst(node);
    }

}
