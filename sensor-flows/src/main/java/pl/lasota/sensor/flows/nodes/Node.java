package pl.lasota.sensor.flows.nodes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.core.exceptions.FlowRuntimeException;
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

    public void execute(LocalContext localContext) throws FlowRuntimeException {
        for (Node node : nodes) {
            if (globalContext.isStopped()) {
                break;
            }
            try {
                log.info("Executing node {} from {} to {}", id, this.getClass().getName(), node.getClass().getName());
                node.execute(localContext);
            } catch (Exception e) {
                log.error("Occurred error during executing node {} from {} to {}", id, this.getClass().getName(), node.getClass().getName());
                throw new RuntimeException(e);
            }
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
