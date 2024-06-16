package pl.lasota.sensor.security.services;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.entities.Provider;
import pl.lasota.sensor.exceptions.AuthException;
import pl.lasota.sensor.member.MemberServiceInterface;
import pl.lasota.sensor.security.AuthServiceInterface;
import pl.lasota.sensor.security.model.SessionStorage;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthServiceInterface {
    private final SessionStorage sessionStorage;
    private final JwtResolve jwtResolve;
    private final MemberServiceInterface memberService;

    @Override
    public String getToken() {
        return sessionStorage.getToken();
    }

    @Override
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

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) throws AuthException {
        final String authorization = request.getHeader(AuthServiceInterface.AUTHORIZATION);
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);
            try {
                Member member = memberService.findByEmailAndProvider(values[0], Provider.LOCAL);
                boolean b = memberService.checkCredential(values[0], values[1], Provider.LOCAL);
                if (!b) {
                    throw new AuthException("Invalid credentials");
                }
                createSession(member);

            } catch (SecurityException e) {
                throw new AuthException("Invalid credentials", e);
            }
        }
    }

    @Override
    public void initialAuthenticationByMemberId(String memberId) throws AuthException {
        try {
            Member member = memberService.getMember(memberId);
            Authentication authentication = new UsernamePasswordAuthenticationToken(member,
                    null,
                    member.getAuthorities());

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        } catch (Exception e) {
            throw new AuthException("Invalid credentials", e);
        }

    }

    @Override
    public void initialAuthenticationByOnlyToken(Map<String, String> request) throws AuthException {

        String token = extractBearerToken(request.get(AUTHORIZATION));

        if (token == null) {
            log.info("Token or session storage is empty ");
            throw new AuthException("Token or session storage is empty ");
        }
        try {
            Jws<Claims> claimsJws = jwtResolve.parseToken(token);
            Member user = memberService.getMember(claimsJws.getPayload().get("id", String.class));

            Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                    token,
                    user.getAuthorities());

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        } catch (Exception e) {
            throw new AuthException("Invalid token", e);
        }
    }

    @Override
    public void initialAuthenticationContextByOnlySession(HttpServletRequest request) throws AuthException {
        if (sessionStorage.isEmpty()) {
            log.info("Session storage is empty {}", request.getServletPath());
            throw new AuthException("Token or session storage is empty " + request.getServletPath());
        }
        try {
            Member user = sessionStorage.getMember();
            Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                    sessionStorage.getToken(),
                    user.getAuthorities());

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        } catch (Exception e) {
            throw new AuthException("Invalid session", e);
        }
    }

    @Override
    public void initiateAuthenticationContext(HttpServletRequest request) throws AuthException {
        String token = extractBearerToken(request.getHeader("Authorization"));

        if (token == null || sessionStorage.isEmpty()) {
            log.info("Token or session storage is empty {}", request.getServletPath());
            throw new AuthException("Token or session storage is empty " + request.getServletPath());
        }
        try {
            jwtResolve.parseToken(token);
            Member user = sessionStorage.getMember();

            Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                    sessionStorage.getToken(),
                    user.getAuthorities());

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        } catch (Exception e) {
            throw new AuthException("Invalid auth", e);
        }
    }


    protected void createSession(Member member) {
        String token = jwtResolve.generateToken(member);
        sessionStorage.setToken(token);
        sessionStorage.setMember(member);
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return null;
    }
}
