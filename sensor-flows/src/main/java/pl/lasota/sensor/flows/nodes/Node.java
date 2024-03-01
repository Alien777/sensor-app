package pl.lasota.sensor.flows.nodes;

import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Node {
    private final List<Node> nodes = new ArrayList<>();
    protected final String id = UUID.randomUUID().toString();

    public void execute(PrivateContext privateContext) throws Exception {
        for (Node node : nodes) {
            node.execute(privateContext);
        }
    }

    public Node addNode(Node node) {
        nodes.add(node);
        return this;
    }
}
