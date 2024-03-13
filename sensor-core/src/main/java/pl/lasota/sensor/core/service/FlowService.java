package pl.lasota.sensor.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.core.exceptions.NotFoundFlowsException;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.flows.Flows;
import pl.lasota.sensor.core.repository.FlowRepository;


@Service
@RequiredArgsConstructor
@Slf4j
public class FlowService {

    private final FlowRepository fr;

    @Transactional
    public Long saveFlows(Long memberId, String config) {
        Flows flows = new Flows();
        flows.setActivate(false);
        flows.setConfig(config);
        Member member = new Member();
        member.setId(memberId);
        flows.setMember(member);
        return fr.save(flows).getId();
    }

    public Flows findFlows(Long memberId, Long flowId) throws NotFoundFlowsException {
        return fr.findFlowsBy(memberId, flowId).orElseThrow(NotFoundFlowsException::new);
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
