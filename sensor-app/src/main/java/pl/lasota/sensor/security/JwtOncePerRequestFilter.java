package pl.lasota.sensor.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.lasota.sensor.configs.SecureConfig;

import java.io.IOException;
import java.util.Arrays;

import static pl.lasota.sensor.configs.SecureConfig.SOCKET_PATH;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtOncePerRequestFilter extends OncePerRequestFilter {
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (SecureConfig.LOGIN_PATH.equals(request.getServletPath()) && request.getMethod().equalsIgnoreCase("POST")) {
            authService.login(request, response);
        } else if (new AntPathMatcher().match(SOCKET_PATH, request.getServletPath())) {
            authService.checkAuthSession(request);
            filterChain.doFilter(request, response);
        } else {
            authService.checkAuthComplete(request);
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(SecureConfig.OPENED_PATHS).toList()
                .stream()
                .anyMatch(s -> new AntPathMatcher().match(s, request.getServletPath()));
    }
}
