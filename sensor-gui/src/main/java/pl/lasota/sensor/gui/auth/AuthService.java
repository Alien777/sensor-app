package pl.lasota.sensor.gui.auth;


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
import pl.lasota.sensor.core.model.Member;
import pl.lasota.sensor.gui.auth.jwt.JwtResolver;
import pl.lasota.sensor.gui.auth.model.SessionStorage;
import pl.lasota.sensor.gui.auth.model.User;
import pl.lasota.sensor.gui.exceptions.AuthException;
import pl.lasota.sensor.gui.model.UserInfo;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final SessionStorage sessionStorage;
    private final JwtResolver jwtResolver;

    public UserInfo getUserDetails() {
        String fullName = sessionStorage.getUser()
                .getFullName();
        Long id = sessionStorage.getUser()
                .getId();

        List<String> roles = sessionStorage.getUser()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new UserInfo(fullName, id, roles);
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


    public void auth(HttpServletRequest request) throws AuthException {
        String token = extractBearerToken(request);
        if (token == null && sessionStorage.isEmpty()) {
            log.info("Anonymous access for system");
            return;
        }

        if (token == null && !sessionStorage.isEmpty()) {
            log.info("Anonymous access for system");
            return;
        }

        if (token != null && sessionStorage.isEmpty()) {
            throw new AuthException("Token is not empty, but session is not empty");
        }

        if (token != null && !sessionStorage.isEmpty()) {
            jwtResolver.parseToken(token);
            User user = sessionStorage.getUser();

            Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                    sessionStorage.getToken(),
                    user.getGrantedRoles());

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            return;
        }

        throw new AuthException("Other auth problem");

    }

    protected void createSession(Member member) {
        User user = User.builder()
                .id(member.getId())
                .username(member.getEmail())
                .fullName(member.getName())
                .isEnabled(member.isEnabled())
                .roles(List.of(member.getRole()))
                .build();
        sessionStorage.setUser(user);
        String token = jwtResolver.generateToken(user);
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
