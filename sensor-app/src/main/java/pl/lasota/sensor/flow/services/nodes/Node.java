package pl.lasota.sensor.flow.services.nodes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.services.nodes.utils.FlowContext;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public abstract class Node {

    @Getter
    private final List<Node> nodes = new ArrayList<>();

    protected final String id;

    protected final GlobalContext globalContext;

    protected FlowContext flowContext;

    protected void fireChildNodes(LocalContext localContext) throws Exception {
        for (Node node : nodes) {
            if (globalContext.isStopped()) {
                break;
            }
            try {
                log.info("Executing node {}", id);
                flowContext.recreateSecurityHolder();
                node.fireChildNodes(localContext);
            } catch (Exception e) {
                throw new SensorFlowException(e, "Occurred error during executing node FROM {} TO {}", id, node.id);
            }
        }
    }

    public void clear() {
        globalContext.stopFlow();
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

    protected void propagateFlowContext(FlowContext flowContext) {
        this.flowContext = flowContext;
        for (Node node : nodes) {
            node.propagateFlowContext(flowContext);
        }
    }
}
