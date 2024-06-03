package pl.lasota.sensor.flow.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.entities.Flow;
import pl.lasota.sensor.member.MemberService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReactivateNodeJob {

    private final FlowDataService flowDataService;
    private final ManagerFlowService mf;
    private final MemberService ms;

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 0 * * ?")
    public void run() {
        List<Flow> flows = flowDataService.findAllActiveFlows();
        flows.forEach(flow -> {
            if (mf.contains(flow.getId())) {
                return;
            }
            ms.auth(flow.getMember());
            try {
                mf.start(flow.getId(), flow.getConfig());
            } catch (Exception e) {
                log.error("Occurred while broadcasting flow ", e);
            }
        });
    }
}
