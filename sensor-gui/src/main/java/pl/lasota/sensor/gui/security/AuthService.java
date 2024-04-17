package pl.lasota.sensor.gui.security;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.gui.exceptions.AuthException;
import pl.lasota.sensor.gui.model.UserInfo;
import pl.lasota.sensor.gui.security.model.SessionStorage;
import pl.lasota.sensor.member.User;
import pl.lasota.sensor.member.entities.Member;
import pl.lasota.sensor.member.entities.Provider;
import pl.lasota.sensor.member.services.MemberService;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final SessionStorage sessionStorage;
    private final JwtResolve jwtResolve;
    private final MemberService memberService;

    public UserInfo getUserDetails() {

        String id = sessionStorage.getUser()
                .getId();

        List<String> roles = sessionStorage.getUser()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new UserInfo(id, roles);
    }

    public String getToken() {
        return sessionStorage.getToken();
    }


    public void logout(HttpServletRequest request, HttpServletResponse response) {
        sessionStorage.clear();
        LogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext()
                .getAuthentication());
        request.getSession()
                .invalidate();

        Cookie cookie = new Cookie("SENSOR_APP_COOKIES", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password

            final String[] values = credentials.split(":", 2);
            try {
                Member member = memberService.findByEmailAndProvider(values[0], Provider.LOCAL);
                boolean b = memberService.checkCredential(values[0], values[1], Provider.LOCAL);
                if (!b) {
                    throw new AuthenticationException();
                }
                createSession(member);

            } catch (SecurityException e) {
                throw new AuthenticationException();
            }
        }
    }


    public void checkAuth(HttpServletRequest request) throws AuthException {
        String token = extractBearerToken(request);
        if (token == null || sessionStorage.isEmpty()) {
            log.info("Token or session storage is empty {}", request.getServletPath());
            throw new AuthException("Token or session storage is empty " + request.getServletPath());
        }

        jwtResolve.parseToken(token);
        User user = sessionStorage.getUser();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                sessionStorage.getToken(),
                user.getGrantedRoles());

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

    }

    protected void createSession(Member member) {
        User user = User.builder()
                .id(member.getId())
                .isEnabled(member.isEnabled())
                .roles(List.of(member.getRole()))
                .build();
        sessionStorage.setUser(user);
        String token = jwtResolve.generateToken(user);
        sessionStorage.setToken(token);
    }


    private String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return null;
    }

}
