package pl.lasota.sensor.flow.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.flow.model.FlowSensorI;
import pl.lasota.sensor.flow.model.FlowStatusI;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.StartFlowNode;
import pl.lasota.sensor.flow.services.nodes.builder.ParserFlows;
import pl.lasota.sensor.flow.services.nodes.nodes.FireOnceNode;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.SensorListeningManager;
import pl.lasota.sensor.member.MemberService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerFlowService {

    private final SensorListeningManager slm;
    private final ParserFlows pf;
    private final FlowDataService fs;
    private final MemberService ms;

    private final Map<Long, ActiveFlow> startedFlow = new ConcurrentHashMap<>();

    public void broadcast(FlowSensorI sensor) {
        slm.broadcast(sensor);
    }

    public FlowStatusI start(Long id, String config, Class<Node> asRoot) {
        if (startedFlow.containsKey(id)) {
            return FlowStatusI.IS_ACTIVE_ALREADY;
        }
        fs.activateFlow(id);
        log.info("Start flow by id {} ", id);
        try {

            List<Node> flows = pf.flows(config, new GlobalContext(ms.loggedUser()));
            if (flows.isEmpty()) {
                throw new IllegalArgumentException("Not found nodes");
            }
            startedFlow.put(id, new ActiveFlow(flows, config));

            boolean isError = flows.parallelStream()
                    .filter(node -> (asRoot == null) != (node instanceof FireOnceNode))
                    .map(node -> ((StartFlowNode) node).start())
                    .anyMatch(aBoolean -> !aBoolean);
            if (isError) {
                flows.forEach(Node::clear);
                startedFlow.remove(id);
                fs.deactivateFlow(id);
                return FlowStatusI.ERROR;
            }

        } catch (Exception e) {
            log.error("Problem with start flow ", e);
            startedFlow.remove(id);
            fs.deactivateFlow(id);
            return FlowStatusI.ERROR;
        }

        return FlowStatusI.OK;
    }

    public FlowStatusI start(Long id, String config) {
        return start(id, config, null);
    }

    public FlowStatusI stop(Long id) {
        if (!startedFlow.containsKey(id)) {
            return FlowStatusI.NOT_FOUND;
        }
        ActiveFlow activeFlow = startedFlow.get(id);
        try {
            activeFlow.getRoots().forEach(Node::clear);
            startedFlow.remove(id);
        } catch (Exception e) {
            return FlowStatusI.ERROR;
        }
        fs.deactivateFlow(id);
        return FlowStatusI.OK;
    }

    public boolean contains(Long id) {
        return startedFlow.containsKey(id);
    }

    @Getter
    @AllArgsConstructor
    public static class ActiveFlow {
        private List<Node> roots;
        private String config;
    }

}
