package pl.lasota.sensor.internal.apis.security;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.internal.apis.exceptions.InternalAuthException;
import pl.lasota.sensor.member.User;
import pl.lasota.sensor.member.entities.Member;
import pl.lasota.sensor.member.services.MemberService;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternalAuthService {

    private final InternalJwtResolve internalJwtResolve;
    private final MemberService memberService;

    public void auth(String memberId)   {
        Member member = memberService.getMember(memberId);
        User user = User.builder().isEnabled(true)
                .id(member.getId())
                .roles(Collections.singleton(member.getRole())).build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                null,
                user.getGrantedRoles());

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    public void auth(HttpServletRequest request) throws InternalAuthException {
        String token = extractBearerToken(request);

        if (token == null) {
            throw new InternalAuthException("Problem with token");
        }
        User user = internalJwtResolve.parseToken(token);
        Collection<? extends GrantedAuthority> grantedRoles = user.getGrantedRoles();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                token,
                grantedRoles);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("AuthorizationInternal");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return null;
    }


}
