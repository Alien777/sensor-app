package pl.lasota.sensor.flow.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.entities.Flow;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.FlowApiInterface;
import pl.lasota.sensor.flow.model.FlowI;
import pl.lasota.sensor.flow.model.FlowSaveI;
import pl.lasota.sensor.member.MemberLoginDetailsServiceInterface;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class FlowGatewayApi implements FlowApiInterface {

    private final ManagerFlowService managerFlowService;
    private final FlowDataService flowDataService;
    private final MemberLoginDetailsServiceInterface ms;

    @Override
    public void save(FlowSaveI flowSaveI) throws SensorFlowException {
        Member member = ms.loggedMember();

        Optional<Flow> flowsInDb = flowDataService.findFlows(member.getId(), flowSaveI.id());
        if (flowsInDb.isEmpty()) {
            flowDataService.saveFlows(member.getId(), null, flowSaveI.name(), flowSaveI.config());
        } else {
            boolean isRunning = flowsInDb.get().isActivate();
            this.disabling(flowSaveI.id());
            flowDataService.saveFlows(member.getId(), flowSaveI.id(), flowSaveI.name(), flowSaveI.config());
            if (isRunning) {
                this.enabling(flowSaveI.id());
            }
        }
    }

    @Override
    public void enabling(Long id) {
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowDataService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            throw new SensorFlowException("Not run flow by id: {}  because not existing in db", id);
        }

        Flow flow = flowsInDb.get();
        if (managerFlowService.contains(id)) {
            return;
        }

        managerFlowService.start(id, flow.getConfig());
    }


    @Override
    public void disabling(Long id) throws SensorFlowException {
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowDataService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            throw new SensorFlowException("Not stop flow by id: {}  because not existing in db", id);
        }

        if (managerFlowService.contains(id)) {
            managerFlowService.stop(id);
        }

    }

    @Override
    public void fireOnce(Long id) throws SensorFlowException {
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowDataService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            throw new SensorFlowException("Not Fire Once flow by id: {} because not existing in db", id);
        }
        Flow flow = flowsInDb.get();
        managerFlowService.start(id, flow.getConfig());
    }

    @Override
    public void delete(Long id) {
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowDataService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            throw new SensorFlowException("Not delete flow by id: {} because not existing in db", id);
        }
        if (managerFlowService.contains(id)) {
            managerFlowService.stop(id);
            flowDataService.delete(id);
        }
    }

    @Override
    public FlowI get(Long id) {
        Member member = ms.loggedMember();
        Flow flow = flowDataService.findFlows(member.getId(), id).orElseThrow(() -> new SensorFlowException("Not found flows by {}", id));
        return new FlowI(flow.getId(), flow.getName(), flow.isActivate(), flow.getConfig());
    }

    @Override
    public List<FlowI> get() {
        Member member = ms.loggedMember();
        return flowDataService.getAll(member.getId()).stream().map(flow -> new FlowI(flow.getId(), flow.getName(), flow.isActivate(), flow.getConfig()))
                .collect(Collectors.toList());
    }
}
