package pl.lasota.sensor.api.mqtt.filter.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.filter.Chain;
import pl.lasota.sensor.api.mqtt.filter.Context;
import pl.lasota.sensor.api.mqtt.filter.Filter;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.service.MemberService;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class IsExistMemberFilter implements Filter<MessageFrame, MessageFrame> {

    public static final int MAC_SIZE = 12;
    public static final int MEMBER_KEY_SIZE = 16;

    private final MemberService ms;

    @Override
    public void execute(MessageFrame request, Context context, Chain<MessageFrame> chain) {
        if (request.getMemberKey().trim().isBlank() || request.getMemberKey().length() != MEMBER_KEY_SIZE) {
            log.info("Member key is wrong {}", request.getMemberKey());
            return;
        }

        if (request.getDeviceKey().trim().isBlank() || request.getDeviceKey().length() != MAC_SIZE) {
            log.info("Device key is wrong {} ", request.getDeviceKey());
            return;
        }

        if (request.getVersion().trim().isBlank()) {
            log.info("Version of fzirmware is obligatory {} ", request.getDeviceKey());
            return;
        }

        if (ms.isMemberExistByMemberKey(request.getMemberKey())) {
            chain.doFilter(request);
        } else {
            log.info("Member key doesn't exist");
        }
    }
}
