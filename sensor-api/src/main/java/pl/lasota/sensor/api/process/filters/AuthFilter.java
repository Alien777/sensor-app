package pl.lasota.sensor.api.process.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.payload.MessageFrame;
import pl.lasota.sensor.api.process.Chain;
import pl.lasota.sensor.api.process.Filter;
import pl.lasota.sensor.api.process.FilterContext;
import pl.lasota.sensor.member.User;
import pl.lasota.sensor.member.entities.Member;
import pl.lasota.sensor.member.services.MemberService;

import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class AuthFilter implements Filter<MessageFrame, MessageFrame> {

    private final MemberService ms;

    @Override
    public void execute(MessageFrame request, FilterContext context, Chain<MessageFrame> chain) throws Exception {
        Member member =  ms.getMember(request.getMemberId());
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
