package pl.lasota.sensor.flow.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.NodeStart;
import pl.lasota.sensor.flow.services.nodes.builder.ParserFlows;
import pl.lasota.sensor.flow.services.nodes.nodes.FireOnceNode;
import pl.lasota.sensor.flow.services.nodes.utils.FlowContext;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.member.MemberLoginDetailsServiceInterface;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerFlowService {

    private final ParserFlows pf;
    private final FlowDataService fs;
    private final MemberLoginDetailsServiceInterface ms;

    private final Map<Long, ActiveFlow> startedFlow = new ConcurrentHashMap<>();

    public void start(Long id, String config) {
        if (startedFlow.containsKey(id)) {
            return;
        }
        fs.activateFlow(id);
        log.info("Start flow id {} config {}", id, config);
        try {
            List<Node> flows = pf.flows(config, new GlobalContext(id));

            startedFlow.put(id, new ActiveFlow(flows, config));
            for (Node node : flows) {
                if (node == null) {
                    continue;
                }
                if (!(node instanceof FireOnceNode)) {
                    ((NodeStart) node).initiate(new FlowContext(ms.loggedMember()));
                }
            }

        } catch (Exception e) {
            startedFlow.remove(id);
            fs.deactivateFlow(id);
            throw new SensorFlowException(e, "Problem with start flows {} ", id);
        }
    }


    public void stop(Long id) {
        if (!startedFlow.containsKey(id)) {
            return;
        }
        ActiveFlow activeFlow = startedFlow.get(id);
        try {
            activeFlow.getRoots().forEach(Node::clear);
            startedFlow.remove(id);
        } catch (Exception e) {
            throw new SensorFlowException(e, "Problem with stop flow {}", id);
        }
        fs.deactivateFlow(id);
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
