package pl.lasota.sensor.flow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.entities.Flow;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.services.FlowDataService;
import pl.lasota.sensor.flow.services.ManagerFlowService;
import pl.lasota.sensor.flow.model.FlowI;
import pl.lasota.sensor.flow.model.FlowSaveI;
import pl.lasota.sensor.flow.model.FlowSensorI;
import pl.lasota.sensor.flow.model.FlowStatusI;
import pl.lasota.sensor.member.MemberService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class FlowApi implements FlowApiInterface {

    private final ManagerFlowService managerFlowService;
    private final FlowDataService flowDataService;
    private final MemberService ms;

    @Override
    public FlowStatusI save(FlowSaveI flowSaveI) throws Exception {
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
        return FlowStatusI.OK;
    }

    @Override
    public FlowStatusI enabling(Long id) {
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowDataService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            log.info("Not run flow by id: {}  because not existing in db", id);
            return FlowStatusI.NOT_FOUND;
        }

        Flow flow = flowsInDb.get();
        if (managerFlowService.contains(id)) {
            log.info("Flow is already executing id: {} on server {}", id);
            return FlowStatusI.OK;
        }

        return managerFlowService.start(id, flow.getConfig());
    }


    @Override
    public FlowStatusI disabling(Long id) throws Exception {
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowDataService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            log.info("Not run flow by id: {} because not existing in db", id);
            return FlowStatusI.NOT_FOUND;
        }

        if (managerFlowService.contains(id)) {
            managerFlowService.stop(id);
            return FlowStatusI.OK;
        }

        return FlowStatusI.OK;
    }

    @Override
    public FlowStatusI fireOnce(Long id) throws Exception {
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowDataService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            log.info("Not delete flow by id: {} because not existing in db", id);
            return FlowStatusI.NOT_FOUND;
        }
        Flow flow = flowsInDb.get();
        managerFlowService.start(id, flow.getConfig());
        return null;
    }

    @Override
    public FlowStatusI delete(Long id) {
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowDataService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            log.info("Not delete flow by id: {} because not existing in db", id);
            return FlowStatusI.NOT_FOUND;
        }
        if (managerFlowService.contains(id)) {
            managerFlowService.stop(id);
            flowDataService.delete(id);
            return FlowStatusI.OK;
        }

        return FlowStatusI.OK;
    }

    @Override
    public FlowI get(Long id) {
        Member member = ms.loggedMember();
        Flow flow = flowDataService.findFlows(member.getId(), id).orElseThrow(() -> new SensorFlowException("Not found flows {}", id));
        return new FlowI(flow.getId(), flow.getName(), flow.isActivate(), flow.getConfig());
    }

    @Override
    public List<FlowI> get() {
        Member member = ms.loggedMember();
        return flowDataService.getAll(member.getId()).stream().map(flow -> new FlowI(flow.getId(), flow.getName(), flow.isActivate(), flow.getConfig()))
                .collect(Collectors.toList());
    }



    @Override
    public void valueOfSensor(FlowSensorI sensor) {
        Member member = ms.loggedMember();
        log.info("Receiver data form {}", sensor.getDeviceId());
        managerFlowService.broadcast(sensor);
    }

}
