package pl.lasota.sensor.gui.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.flows.Flow;
import pl.lasota.sensor.core.restapi.SensorFlowsEndpoint;
import pl.lasota.sensor.core.restapi.SensorFlowsHelper;
import pl.lasota.sensor.core.service.FlowService;
import pl.lasota.sensor.core.service.MemberService;
import pl.lasota.sensor.gui.model.FlowSaveT;
import pl.lasota.sensor.gui.model.FlowT;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flow")
@Slf4j
@RequiredArgsConstructor
public class FlowsController {
    private final FlowService flowService;
    private final MemberService ms;
    private final SensorFlowsEndpoint sfe;
    private final SensorFlowsHelper sfh;


    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public void save(@RequestBody FlowSaveT flowT) throws Exception {
        Member member = ms.loggedUser();
        flowService.saveFlows(member.getId(), FlowSaveT.map(flowT));
    }

    @PostMapping("/start/{id}")
    @PreAuthorize("isAuthenticated()")
    public void startFlows(@PathVariable("id") Long id) throws Exception {
        Member member = ms.loggedUser();
        Flow flow = flowService.findFlows(member.getId(), id);
        sfe.startFlows(flow.getId(), flow.getConfig());
    }

    @DeleteMapping("/stop/{id}")
    @PreAuthorize("isAuthenticated()")
    public void stopFlows(@PathVariable("id") Long id) throws Exception {
        Member member = ms.loggedUser();
        flowService.findFlows(member.getId(), id);
        sfh.stopFlowsAllInstance(id);
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public List<FlowT> getAll() throws Exception {
        Member member = ms.loggedUser();
        return flowService.getAll(member.getId()).stream().map(FlowT::map).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public FlowT get(@PathVariable("id") Long id) throws Exception {
        Member member = ms.loggedUser();
        return FlowT.map(flowService.findFlows(member.getId(), id));
    }

}
