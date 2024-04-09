package pl.lasota.sensor.flows.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import pl.lasota.sensor.core.apis.FlowsMicroserviceEndpoint;
import pl.lasota.sensor.core.apis.model.flow.FlowSaveT;
import pl.lasota.sensor.core.apis.model.flow.FlowSensorT;
import pl.lasota.sensor.core.apis.model.flow.FlowStatusT;
import pl.lasota.sensor.core.apis.model.flow.FlowT;
import pl.lasota.sensor.core.entities.Member;
import pl.lasota.sensor.core.entities.flows.Flow;
import pl.lasota.sensor.core.exceptions.NotFoundFlowsException;
import pl.lasota.sensor.core.exceptions.NotFoundMemberException;
import pl.lasota.sensor.core.service.MemberService;
import pl.lasota.sensor.flows.LocalManagerFlows;
import pl.lasota.sensor.flows.configs.FlowProperties;
import pl.lasota.sensor.flows.service.FlowService;

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
    private final FlowProperties fp;

    @Override
    public FlowStatusT save(FlowSaveT flowSaveT) throws Exception {
        log.info("Save flow {}", flowSaveT.getId());
        Member member = ms.loggedMember();

        Optional<Flow> flowsInDb = flowService.findFlows(member.getId(), flowSaveT.getId());
        if (flowsInDb.isEmpty()) {
            flowService.saveFlows(member.getId(), null, flowSaveT.getName(), flowSaveT.getConfig());
        } else {
            this.stop(flowSaveT.getId());
            flowService.saveFlows(member.getId(), flowSaveT.getId(), flowSaveT.getName(), flowSaveT.getConfig());
            this.start(flowSaveT.getId());
        }
        return FlowStatusT.OK;
    }

    @Override
    public FlowStatusT start(Long id) throws NotFoundMemberException, NotFoundFlowsException {
        log.info("Start flow id: {}", id);
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            log.info("Not run flow by id: {}  because not existing in db", id);
            return FlowStatusT.NOT_FOUND;
        }

        Flow flow = flowsInDb.get();
        if (localManagerFlows.contains(id)) {
            log.info("Flow is already executing id: {} on server {}", id, fp.getInstanceId());
            return FlowStatusT.OK;
        }

        Optional<String> idServerWhenMaybeExecute = broadcastWhoExecuteFlow(id, dc, restClient, fp.getInstanceId());
        if (idServerWhenMaybeExecute.isPresent()) {
            log.info("Flow is already executing id: {} on server {}", id, idServerWhenMaybeExecute.get());
            return FlowStatusT.OK;
        }

        return localManagerFlows.start(id, flow.getConfig());
    }


    @Override
    public FlowStatusT stop(Long id) throws Exception {
        log.info("Stop flow id: {} ", id);
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            log.info("Not run flow by id: {} because not existing in db", id);
            return FlowStatusT.NOT_FOUND;
        }

        if (localManagerFlows.contains(id)) {
            localManagerFlows.stop(id);
            return FlowStatusT.OK;
        }

        Optional<String> idServerWhenMaybeExecute = broadcastWhoExecuteFlow(id, dc, restClient, fp.getInstanceId());
        Flow flow = flowsInDb.get();
        if (idServerWhenMaybeExecute.isPresent()) {
            return stopOnIndicateServer(flow.getId(), idServerWhenMaybeExecute.get(), dc, restClient);
        } else {
            return FlowStatusT.OK;
        }
    }

    @Override
    public FlowStatusT delete(Long id) throws NotFoundFlowsException, NotFoundMemberException {
        log.info("delete flow  id {} ", id);
        Member member = ms.loggedMember();
        Optional<Flow> flowsInDb = flowService.findFlows(member.getId(), id);
        if (flowsInDb.isEmpty()) {
            log.info("Not delete flow by id: {} because not existing in db", id);
            return FlowStatusT.NOT_FOUND;
        }
        if (localManagerFlows.contains(id)) {
            localManagerFlows.stop(id);
            flowService.delete(id);
            return FlowStatusT.OK;
        }

        Optional<String> idServerWhenMaybeExecute = broadcastWhoExecuteFlow(id, dc, restClient, fp.getInstanceId());

        if (idServerWhenMaybeExecute.isPresent()) {
            FlowStatusT flowStatusT = stopOnIndicateServer(id, idServerWhenMaybeExecute.get(), dc, restClient);
            if (flowStatusT.equals(FlowStatusT.OK)) {
                flowService.delete(id);
                return FlowStatusT.OK;
            } else {
                return FlowStatusT.ERROR;
            }
        }
        flowService.delete(id);
        return FlowStatusT.OK;
    }

    @Override
    public FlowT get(Long id) throws NotFoundMemberException, NotFoundFlowsException {
        log.info("Get flow from {}", id);
        Member member = ms.loggedMember();
        return FlowT.map(flowService.findFlows(member.getId(), id).orElseThrow(NotFoundFlowsException::new));
    }

    @Override
    public List<FlowT> get() throws NotFoundMemberException {
        log.info("Get all flows");
        Member member = ms.loggedMember();
        return flowService.getAll(member.getId()).stream().map(FlowT::map).collect(Collectors.toList());
    }

    @Override
    public String doesExecuteFlowId(Long id) throws NotFoundMemberException {
        ms.loggedMember();
        log.info("Looking for flow if {}", id);
        if (localManagerFlows.contains(id)) {
            return fp.getInstanceId();
        }
        return null;
    }

    @Override
    public void valueOfSensor(FlowSensorT sensor) throws NotFoundMemberException {
        ms.loggedMember();
        log.info("Receiver data form {}", sensor.getDeviceId());
        localManagerFlows.broadcast(sensor);
    }

}
