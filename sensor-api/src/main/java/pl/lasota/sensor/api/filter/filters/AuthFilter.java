package pl.lasota.sensor.api.filter.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.filter.Chain;
import pl.lasota.sensor.api.filter.Context;
import pl.lasota.sensor.api.filter.Filter;
import pl.lasota.sensor.core.common.User;
import pl.lasota.sensor.core.entities.Member;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.exceptions.SensorException;
import pl.lasota.sensor.core.service.MemberService;

import java.util.Collections;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class AuthFilter implements Filter<MessageFrame, MessageFrame> {

    private final MemberService ms;

    @Override
    public void execute(MessageFrame request, Context context, Chain<MessageFrame> chain) throws Exception {

        Optional<Member> memberOptional = ms.getMember(request.getMemberId());
        if (memberOptional.isEmpty()) {
            throw new SensorException("Problem with auth");
        }
        Member member = memberOptional.get();
        User user = User.builder().isEnabled(true)
                .id(request.getMemberId())
                .roles(Collections.singleton(member.getRole())).build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                request.getToken(),
                user.getGrantedRoles());

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        chain.doFilter(request);
    }
}
