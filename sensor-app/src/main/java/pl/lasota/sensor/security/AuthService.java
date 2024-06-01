package pl.lasota.sensor.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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
import org.springframework.web.socket.WebSocketSession;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.entities.Provider;
import pl.lasota.sensor.exceptions.AuthException;
import pl.lasota.sensor.gateway.gui.model.UserInfo;
import pl.lasota.sensor.member.MemberService;
import pl.lasota.sensor.security.model.SessionStorage;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final SessionStorage sessionStorage;
    private final JwtResolve jwtResolve;
    private final MemberService memberService;

    private final static ConcurrentHashMap<String, Member> IS_LOGGED = new ConcurrentHashMap<>();

    public UserInfo getUserDetails() {

        String id = sessionStorage.getMember()
                .getId();

        List<String> roles = sessionStorage.getMember()
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

    public void checkAuth(Map<String, String> request, String id) {

        String token = extractBearerToken(request);
        if (token == null) {
            log.info("Token or session storage is empty");
            throw new AuthException("Token or session storage is empty ");
        }
        Jws<Claims> claimsJws = jwtResolve.parseToken(token);
        memberService.auth((String) claimsJws.getPayload().get("id"));
        IS_LOGGED.put(id, memberService.loggedUser());
    }


    public void checkAuth(HttpServletRequest request) throws AuthException {
        String token = extractBearerToken(request);

        if (token == null || sessionStorage.isEmpty()) {
            log.info("Token or session storage is empty {}", request.getServletPath());
            throw new AuthException("Token or session storage is empty " + request.getServletPath());
        }
        jwtResolve.parseToken(token);
        Member user = sessionStorage.getMember();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                sessionStorage.getToken(),
                user.getAuthorities());

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }


    protected void createSession(Member member) {
        String token = jwtResolve.generateToken(member);
        sessionStorage.setToken(token);
        sessionStorage.setMember(member);
    }


    private String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return null;
    }

    private String extractBearerToken(Map<String, String> request) {
        String authorizationHeader = request.get("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return null;
    }


    public void logout(WebSocketSession session) {
        IS_LOGGED.remove(session.getId());
        try {
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Member getSessionUser(WebSocketSession session) {
        return IS_LOGGED.get(session.getId());
    }
}
