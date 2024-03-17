package pl.lasota.sensor.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.core.exceptions.NotFoundFlowsException;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.flows.Flow;
import pl.lasota.sensor.core.repository.FlowRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class FlowService {

    private final FlowRepository fr;

    @Transactional
    public Long saveFlows(Long memberId, Flow flow) {
        Member member = new Member();
        member.setId(memberId);
        flow.setMember(member);
        return fr.save(flow).getId();
    }

    public Flow findFlows(Long memberId, Long flowId) throws NotFoundFlowsException {
        return fr.findFlowsBy(memberId, flowId).orElseThrow(NotFoundFlowsException::new);
    }

    public List<Flow> getAll(Long memberId) {
        return fr.findFlowsBy(memberId).stream().toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void activateFlow(Long id) {
        fr.activate(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deactivateFlow(Long id) {
        fr.deactivate(id);
    }
}
