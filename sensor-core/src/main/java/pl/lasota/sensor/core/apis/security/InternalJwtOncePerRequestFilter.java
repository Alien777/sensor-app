package pl.lasota.sensor.core.apis.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.lasota.sensor.core.exceptions.InternalAuthException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class InternalJwtOncePerRequestFilter extends OncePerRequestFilter {

    private final InternalAuthService auth;

    private final List<String> skip;

    public InternalJwtOncePerRequestFilter(InternalAuthService auth) {
        this(auth, new ArrayList<>());
    }

    public InternalJwtOncePerRequestFilter(InternalAuthService auth, List<String> skip) {
        this.auth = auth;
        this.skip = skip;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return skip.stream().anyMatch(s -> new AntPathMatcher().match(s, request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            auth.auth(request);
        } catch (InternalAuthException e) {
            throw new RuntimeException(e);
        }
        filterChain.doFilter(request, response);
    }
}
