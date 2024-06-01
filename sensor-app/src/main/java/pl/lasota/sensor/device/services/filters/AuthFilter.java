package pl.lasota.sensor.device.services.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.member.MemberService;
import pl.lasota.sensor.payload.MessageFrame;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class AuthFilter implements Filter<MessageFrame, MessageFrame> {

    private final MemberService ms;

    @Override
    public void execute(MessageFrame request, FilterContext context, Chain<MessageFrame> chain) throws Exception {
        ms.auth(request.getMemberId());
        chain.doFilter(request);
    }
}
