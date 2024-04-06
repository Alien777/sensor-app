package pl.lasota.sensor.flows.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.core.exceptions.NotFoundFlowsException;
import pl.lasota.sensor.core.entities.Member;
import pl.lasota.sensor.core.entities.flows.Flow;

import java.time.OffsetDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class FlowService {

    private final FlowRepository fr;

    @Transactional
    public Flow saveFlows(String memberId, Long flowId, String name, String config) {
        Member member = new Member();
        member.setId(memberId);
        Flow flow = new Flow();
        flow.setMember(member);
        flow.setName(name);
        flow.setConfig(config);
        flow.setId(flowId);
        flow.setUpdated(OffsetDateTime.now());
        return fr.save(flow);
    }

    public Flow findFlows(String memberId, Long flowId) throws NotFoundFlowsException {
        return fr.findFlowsBy(memberId, flowId).orElseThrow(NotFoundFlowsException::new);
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
}
