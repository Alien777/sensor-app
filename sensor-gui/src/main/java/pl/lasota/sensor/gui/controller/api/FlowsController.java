package pl.lasota.sensor.gui.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.flows.Flows;
import pl.lasota.sensor.core.restapi.SensorFlowsEndpoint;
import pl.lasota.sensor.core.restapi.SensorFlowsHelper;
import pl.lasota.sensor.core.service.FlowService;
import pl.lasota.sensor.core.service.MemberService;

@RestController()
@RequestMapping("/api/flow")
@RequiredArgsConstructor
@Slf4j
public class FlowsController {
    private final FlowService flowService;
    private final MemberService ms;
    private final SensorFlowsEndpoint sfe;
    private final SensorFlowsHelper sfh;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    private void save(String config) throws Exception {
        Member member = ms.loggedUser();
        flowService.saveFlows(member.getId(), config);
    }

    @PostMapping("/start")
    @PreAuthorize("isAuthenticated()")
    private void startFlows(Long id) throws Exception {
        Member member = ms.loggedUser();
        Flows flows = flowService.findFlows(member.getId(), id);
        sfe.startFlows(flows.getId(), flows.getConfig());
    }

    @DeleteMapping()
    @PreAuthorize("isAuthenticated()")
    private void stopFlows(Long id) throws Exception {
        Member member = ms.loggedUser();
        flowService.findFlows(member.getId(), id);
        sfh.stopFlowsAllInstance(id);
    }

}
