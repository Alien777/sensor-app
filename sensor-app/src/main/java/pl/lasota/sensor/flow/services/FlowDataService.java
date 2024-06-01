package pl.lasota.sensor.flow.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.entities.Flow;
import pl.lasota.sensor.flow.services.repositories.FlowRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class FlowDataService {

    private final FlowRepository fr;

    @Transactional
    public Flow saveFlows(String memberId, Long flowId, String name, String config) {

        Flow flow = new Flow();
        flow.setMember(memberId);
        flow.setName(name);
        flow.setConfig(config);
        flow.setId(flowId);
        flow.setUpdated(OffsetDateTime.now());
        return fr.save(flow);
    }

    public Optional<Flow> findFlows(String memberId, Long flowId) {
        return fr.findFlowsBy(memberId, flowId);
    }

    public List<Flow> getAll(String memberId) {
        return fr.findFlowsBy(memberId).stream().toList();
    }

    @Transactional
    public void activateFlow(Long id) {
        fr.activate(id);
    }

    @Transactional
    public void deactivateFlow(Long id) {
        fr.deactivate(id);
    }

    @Transactional
    public void delete(Long flowId) {
        fr.deleteById(flowId);
    }

    public List<Flow> findAllActiveFlows() {
        return fr.findAllActiveFlows();
    }
}
