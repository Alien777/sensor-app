package pl.lasota.sensor.flows.job;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.client.RestClient;
import pl.lasota.sensor.core.apis.FlowsMicroserviceEndpoint;
import pl.lasota.sensor.core.apis.security.InternalAuthService;
import pl.lasota.sensor.core.entities.flows.Flow;
import pl.lasota.sensor.flows.LocalManagerFlows;
import pl.lasota.sensor.flows.configs.FlowProperties;
import pl.lasota.sensor.flows.service.FlowService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class ReactiveJob extends QuartzJobBean {

    private final FlowService flowService;
    private final DiscoveryClient dc;
    private final RestClient rt;
    private final FlowProperties fp;
    private final FlowsMicroserviceEndpoint fme;
    private final LocalManagerFlows mf;
    private final InternalAuthService ms;

    @Override
    protected void executeInternal(org.quartz.JobExecutionContext context) throws JobExecutionException {
        List<Flow> flows = flowService.findAllActiveFlows();
        flows.forEach(flow -> {
            if (mf.contains(flow.getId())) {
                return;
            }
            ms.auth(flow.getMember());
            Optional<String> s = fme.broadcastWhoExecuteFlow(flow.getId(), dc, rt, fp.getInstanceId());
            if(s.isEmpty()) {
                try {
                    fme.start(flow.getId());
                } catch (Exception e) {
                    log.error("Occurred while broadcasting flow ", e);
                }
            }
        });
    }


}
