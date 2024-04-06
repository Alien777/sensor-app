package pl.lasota.sensor.flows;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.apis.model.flow.FlowSensorT;
import pl.lasota.sensor.core.apis.model.flow.FlowStatusT;
import pl.lasota.sensor.core.entities.sensor.Sensor;
import pl.lasota.sensor.flows.service.FlowService;
import pl.lasota.sensor.flows.configs.FlowProperties;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.StartFlowNode;
import pl.lasota.sensor.flows.nodes.builder.NodeCreatorFactory;
import pl.lasota.sensor.flows.nodes.builder.ParserFlows;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerFlows {

    private final SensorListeningManager slm;
    private final ParserFlows pf;
    private final NodeCreatorFactory ncf;
    private final FlowService fs;
    private final FlowProperties eurekaId;
    private final Map<Long, ActiveFlow> startedFlow = new ConcurrentHashMap<>();

    public void broadcast(FlowSensorT sensor) {
        slm.broadcast(sensor);
    }

    public FlowStatusT start(Long id, String config) {
        if (startedFlow.containsKey(id)) {
            return FlowStatusT.IS_ACTIVE_ALREADY;
        }
        fs.activateFlow(id);
        log.info("Start flow by id {} on {}", id, eurekaId.getInstanceId());
        try {
            NodeCreatorFactory.Factory factory = ncf.create();
            List<Node> flows = pf.flows(config, factory);
            startedFlow.put(id, new ActiveFlow(flows, config));

            boolean isError = flows.parallelStream()
                    .map(node -> ((StartFlowNode) node).start())
                    .anyMatch(aBoolean -> !aBoolean);
            if (isError) {
                flows.forEach(Node::clear);
                startedFlow.remove(id);
                fs.deactivateFlow(id);
                return FlowStatusT.ERROR;
            }

        } catch (Exception e) {
            log.error("Problem with start flow ", e);
            startedFlow.remove(id);
            fs.deactivateFlow(id);
            return FlowStatusT.ERROR;
        }

        return FlowStatusT.OK;
    }

    public FlowStatusT stop(Long id) {
        if (!startedFlow.containsKey(id)) {
            return FlowStatusT.NOT_FOUND;
        }
        ActiveFlow activeFlow = startedFlow.get(id);
        try {
            activeFlow.getRoots().forEach(Node::clear);
            startedFlow.remove(id);
        } catch (Exception e) {
            return FlowStatusT.ERROR;
        }
        fs.deactivateFlow(id);
        return FlowStatusT.OK;
    }

    public boolean contains(Long id) {
        return startedFlow.containsKey(id);
    }
}
