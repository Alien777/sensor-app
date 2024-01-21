package pl.lasota.sensor.api.mqtt.filter.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.filter.Chain;
import pl.lasota.sensor.api.mqtt.filter.Filter;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.service.MemberService;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class IsExistMemberFilter implements Filter<MessageFrame, MessageFrame> {

    private final MemberService ms;

    @Override
    public void execute(MessageFrame request, Chain<MessageFrame> chain) {
        if (request.getMemberKey().trim().isBlank()) {
            log.info("Not found member data");
            return;
        }
        if (ms.isMemberExistByMemberKey(request.getMemberKey())) {
            chain.doFilter(request);
        }else {
            log.info("Member doesn't exist");
        }
    }
}
