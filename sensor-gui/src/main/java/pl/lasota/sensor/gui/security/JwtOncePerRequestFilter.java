package pl.lasota.sensor.gui.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.lasota.sensor.gui.config.SecureConfig;

import java.io.IOException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtOncePerRequestFilter extends OncePerRequestFilter {
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecureConfig.LOGIN_PATH.equals(request.getServletPath()) && request.getMethod().equalsIgnoreCase("POST")) {
            authService.login(request, response);
        } else {
            authService.checkAuth(request);
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
