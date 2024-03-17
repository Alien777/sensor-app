package pl.lasota.sensor.flows;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.service.FlowService;
import pl.lasota.sensor.flows.nodes.StartFlowNode;
import pl.lasota.sensor.flows.nodes.builder.NodeCreatorFactory;
import pl.lasota.sensor.flows.nodes.builder.ParserFlows;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ManagerFlows {

    private final SensorListeningManager slm;
    private final ParserFlows pf;
    private final NodeCreatorFactory ncf;
    private final FlowService fs;
    private final Map<Long, ActiveFlow> startedFlow = new ConcurrentHashMap<>();

    public void broadcast(Sensor sensor) {
        slm.broadcast(sensor);
    }

    public boolean start(Long id, String config) {

        fs.activateFlow(id);
        try {
            List<Node> flows = pf.flows(config, ncf.create());

            boolean isError = flows.parallelStream()
                    .map(node -> ((StartFlowNode) node).start())
                    .anyMatch(aBoolean -> !aBoolean);

            if (isError) {
                flows.forEach(Node::clear);
                fs.deactivateFlow(id);
                return false;
            }
            startedFlow.put(id, new ActiveFlow(flows, config));
        } catch (Exception e) {
            fs.deactivateFlow(id);
            return false;
        }
        return true;
    }

    public boolean stop(Long id) {
        ActiveFlow activeFlow = startedFlow.get(id);
        if (activeFlow == null) {
            return false;
        }

        try {
            activeFlow.getRoots().forEach(Node::clear);
            startedFlow.remove(id);
        } catch (Exception e) {
            fs.activateFlow(id);
            return false;
        }
        fs.deactivateFlow(id);
        return true;
    }
}
