package pl.lasota.sensor.flows.nodes.builder;

import pl.lasota.sensor.flows.nodes.nodes.Node;

import java.util.LinkedList;

public class FlowsBuilder {
    private final Node root;

    private FlowsBuilder(Node root) {
        this.root = root;
    }

    public static FlowsBuilder root(Node root) {
        return new FlowsBuilder(root);
    }

    public FlowsBuilder add(Node parent, Node child) {
        Node isFoundNode = findNode(root, parent);
        if (isFoundNode != null) {
            isFoundNode.addNode(child);
        }
        return this;
    }

    private Node findNode(Node root, Node lookingFor) {
        if (root.equals(lookingFor)) {
            return root;
        }

        LinkedList<Node> nodes = new LinkedList<>(root.getNodes());

        while (!nodes.isEmpty()) {
            Node node = nodes.poll();
            if(node.getNodes()!=null)
            {
                nodes.addAll(node.getNodes());
            }
            if (node.equals(lookingFor)) {
                return node;
            }
        }

        return null;
    }


}