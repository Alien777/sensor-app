package pl.lasota.sensor.flows.apis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import pl.lasota.sensor.flows.exceptions.SensorFlowException;
import pl.lasota.sensor.flows.properties.FlowsProperties;
import pl.lasota.sensor.flows.services.LocalManagerFlows;
import pl.lasota.sensor.flows.entities.Flow;
import pl.lasota.sensor.flows.services.FlowService;
import pl.lasota.sensor.internal.apis.api.FlowsMicroserviceEndpoint;
import pl.lasota.sensor.internal.apis.api.flows.FlowI;
import pl.lasota.sensor.internal.apis.api.flows.FlowSaveI;
import pl.lasota.sensor.internal.apis.api.flows.FlowSensorI;
import pl.lasota.sensor.internal.apis.api.flows.FlowStatusI;
import pl.lasota.sensor.member.entities.Member;
import pl.lasota.sensor.member.services.MemberService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FlowsMicroserviceController implements FlowsMicroserviceEndpoint {

    private final LocalManagerFlows localManagerFlows;
    private final FlowService flowService;
    private final MemberService ms;
    private final DiscoveryClient dc;
    private final RestClient restClient;
    private final FlowsProperties fp;

    @Override
    public FlowStatusI save(FlowSaveI flowSaveI) throws Exception {
        Member member = ms.loggedMember();

        Optional<Flow> flowsInDb = flowService.findFlows(member.getId(), flowSaveI.id());
        if (flowsInDb.isEmpty()) {
            flowService.saveFlows(member.getId(), null, flowSaveI.name(), flowSaveI.config());
        } else {
            boolean isRunning = flowsInDb.get().isActivate();
            this.stop(flowSaveI.id());
            flowService.saveFlows(member.getId(), flowSaveI.id(), flowSaveI.name(), flowSaveI.config());
            if (isRunning) {
                this.start(flowSaveI.id());
            }
        }
        return FlowStatusI.OK;
    }

    @Override
    public FlowStatusI start(Long id) {
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            log.info("Not run flow by id: {}  because not existing in db", id);
            return FlowStatusI.NOT_FOUND;
        }

        Flow flow = flowsInDb.get();
        if (localManagerFlows.contains(id)) {
            log.info("Flow is already executing id: {} on server {}", id, fp.getInstanceId());
            return FlowStatusI.OK;
        }

        Optional<String> idServerWhenMaybeExecute = broadcastWhoExecuteFlow(id, dc, restClient, fp.getInstanceId());
        if (idServerWhenMaybeExecute.isPresent()) {
            log.info("Flow is already executing id: {} on server {}", id, idServerWhenMaybeExecute.get());
            return FlowStatusI.OK;
        }

        return localManagerFlows.start(id, flow.getConfig());
    }


    @Override
    public FlowStatusI stop(Long id) throws Exception {
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            log.info("Not run flow by id: {} because not existing in db", id);
            return FlowStatusI.NOT_FOUND;
        }

        if (localManagerFlows.contains(id)) {
            localManagerFlows.stop(id);
            return FlowStatusI.OK;
        }

        Optional<String> idServerWhenMaybeExecute = broadcastWhoExecuteFlow(id, dc, restClient, fp.getInstanceId());
        Flow flow = flowsInDb.get();
        if (idServerWhenMaybeExecute.isPresent()) {
            return stopOnIndicateServer(flow.getId(), idServerWhenMaybeExecute.get(), dc, restClient);
        } else {
            return FlowStatusI.OK;
        }
    }

    @Override
    public FlowStatusI delete(Long id) {
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            log.info("Not delete flow by id: {} because not existing in db", id);
            return FlowStatusI.NOT_FOUND;
        }
        if (localManagerFlows.contains(id)) {
            localManagerFlows.stop(id);
            flowService.delete(id);
            return FlowStatusI.OK;
        }

        Optional<String> idServerWhenMaybeExecute = broadcastWhoExecuteFlow(id, dc, restClient, fp.getInstanceId());

        if (idServerWhenMaybeExecute.isPresent()) {
            FlowStatusI flowStatusI = stopOnIndicateServer(id, idServerWhenMaybeExecute.get(), dc, restClient);
            if (flowStatusI.equals(FlowStatusI.OK)) {
                flowService.delete(id);
                return FlowStatusI.OK;
            } else {
                return FlowStatusI.ERROR;
            }
        }
        flowService.delete(id);
        return FlowStatusI.OK;
    }

    @Override
    public FlowI get(Long id) {
        Member member = ms.loggedMember();
        Flow flow = flowService.findFlows(member.getId(), id).orElseThrow(() -> new SensorFlowException("Not found flows {}", id));
        return new FlowI(flow.getId(), flow.getName(), flow.isActivate(), flow.getConfig());
    }

    @Override
    public List<FlowI> get() {
        Member member = ms.loggedMember();
        return flowService.getAll(member.getId()).stream().map(flow -> new FlowI(flow.getId(), flow.getName(), flow.isActivate(), flow.getConfig()))
                .collect(Collectors.toList());
    }

    @Override
    public String doesExecuteFlowId(Long id) {
        ms.loggedMember();
        log.info("Looking for flow if {}", id);
        if (localManagerFlows.contains(id)) {
            return fp.getInstanceId();
        }
        return null;
    }

    @Override
    public void valueOfSensor(FlowSensorI sensor) {
        ms.loggedMember();
        log.info("Receiver data form {}", sensor.getDeviceId());
        localManagerFlows.broadcast(sensor);
    }

}
