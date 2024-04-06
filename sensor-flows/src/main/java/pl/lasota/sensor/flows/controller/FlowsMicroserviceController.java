package pl.lasota.sensor.flows.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pl.lasota.sensor.core.apis.model.flow.FlowSaveT;
import pl.lasota.sensor.core.apis.model.flow.FlowSensorT;
import pl.lasota.sensor.core.apis.model.flow.FlowStatusT;
import pl.lasota.sensor.core.entities.Member;
import pl.lasota.sensor.core.entities.flows.Flow;
import pl.lasota.sensor.core.apis.FlowsMicroserviceEndpoint;
import pl.lasota.sensor.core.apis.model.flow.FlowT;
import pl.lasota.sensor.core.exceptions.NotFoundFlowsException;
import pl.lasota.sensor.core.exceptions.NotFoundMemberException;
import pl.lasota.sensor.core.service.MemberService;
import pl.lasota.sensor.flows.configs.FlowProperties;
import pl.lasota.sensor.flows.service.FlowService;
import pl.lasota.sensor.flows.ManagerFlows;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FlowsMicroserviceController implements FlowsMicroserviceEndpoint {

    private final ManagerFlows managerFlows;
    private final FlowService flowService;
    private final MemberService ms;
    private final DiscoveryClient dc;
    private final RestTemplate rt;
    private final FlowProperties fp;
    private final FlowsMicroserviceEndpoint fme;

    @Override
    public FlowStatusT save(FlowSaveT flowSaveT) throws NotFoundMemberException, NotFoundFlowsException {
        info("save flow", flowSaveT.getId());
        Member member = ms.loggedMember();
        Flow flows = null;
        if (flowSaveT.getId() != null) {
            flows = flowService.findFlows(member.getId(), flowSaveT.getId());
        }
        log.info("Save config: {}", flowSaveT.getConfig());
        flowService.saveFlows(member.getId(), flowSaveT.getId(), flowSaveT.getName(), flowSaveT.getConfig());
        if (flows != null && flows.isActivate()) {
            managerFlows.stop(flows.getId());
            managerFlows.start(flowSaveT.getId(), flowSaveT.getConfig());
        }
        return FlowStatusT.OK;
    }

    @Override
    public FlowStatusT start(Long id) throws NotFoundMemberException, NotFoundFlowsException {
        info("start flow", id);
        Member member = ms.loggedMember();
        Flow flows = flowService.findFlows(member.getId(), id);
        if (flows.isActivate()) {
            return FlowStatusT.IS_ACTIVE_ALREADY;
        }
        return managerFlows.start(id, flows.getConfig());
    }

    @Override
    public FlowStatusT stop(Long id) throws Exception {
        info("stop flow", id);
        Member member = ms.loggedMember();
        Flow flows = flowService.findFlows(member.getId(), id);
        if (!flows.isActivate()) {
            return FlowStatusT.NOT_ACTIVE;
        }

        if (managerFlows.contains(id)) {
            return managerFlows.stop(id);
        }
        List<String> instancesId = fme.findInstanceWhoExecute(id, fp.getInstanceId(), dc, rt);
        boolean b = instancesId.parallelStream().map(s -> fme.stop(id, s, dc, rt))
                .allMatch(flowStatusT -> flowStatusT == FlowStatusT.OK);
        if (b) {
            return FlowStatusT.OK;
        } else {
            return FlowStatusT.ERROR;
        }
    }

    @Override
    public FlowStatusT delete(Long id) throws NotFoundFlowsException, NotFoundMemberException {
        info("delete flow", id);
        Member member = ms.loggedMember();

        if (managerFlows.contains(id)) {
            FlowStatusT stop = managerFlows.stop(id);
            if (stop == FlowStatusT.OK) {
                flowService.delete(id);
                return FlowStatusT.OK;
            }
            return FlowStatusT.ERROR;
        }
        List<String> instancesId = fme.findInstanceWhoExecute(id, fp.getInstanceId(), dc, rt);
        boolean b = instancesId.parallelStream().map(s -> fme.stop(id, s, dc, rt))
                .allMatch(flowStatusT -> flowStatusT == FlowStatusT.OK);
        if (b) {
            flowService.delete(id);
            return FlowStatusT.OK;
        } else {
            return FlowStatusT.ERROR;
        }
    }

    @Override
    public FlowT get(Long id) throws NotFoundMemberException, NotFoundFlowsException {
        info("get flow", id);
        Member member = ms.loggedMember();
        return FlowT.map(flowService.findFlows(member.getId(), id));
    }

    @Override
    public List<FlowT> get() throws NotFoundMemberException {
        info("get all flow");
        Member member = ms.loggedMember();
        return flowService.getAll(member.getId()).stream().map(FlowT::map).collect(Collectors.toList());
    }

    @Override
    public String findInstanceWhoExecute(Long id) {
        info("looking for {}", id);
        if (managerFlows.contains(id)) {
            return fp.getInstanceId();
        }
        return null;
    }

    @Override
    public void sendSensorValue(FlowSensorT sensor) {
        managerFlows.broadcast(sensor);
    }

    private void info(String message, Object... objects) {
        Optional<ServiceInstance> first = dc.getInstances("sensor-flows")
                .stream()
                .filter(instance -> instance.getInstanceId().equals(fp.getInstanceId())).findFirst();
        log.info("{} " + message, first.get().getUri(), objects);

    }
}
