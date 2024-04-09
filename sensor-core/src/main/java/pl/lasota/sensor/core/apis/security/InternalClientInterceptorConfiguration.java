package pl.lasota.sensor.core.apis.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.common.User;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class InternalClientInterceptorConfiguration implements ClientHttpRequestInterceptor, RequestInterceptor {


    private final InternalJwtResolve internalJwtResolve;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            String token = internalJwtResolve.generateToken(user.getId(), user.getRoles());
            request.getHeaders().add("AuthorizationInternal", "Bearer " + token);
        }
        return execution.execute(request, body);
    }

    @Override
    public void apply(RequestTemplate template) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        String token = internalJwtResolve.generateToken(principal.getId(), principal.getRoles());
        template.header("AuthorizationInternal", "Bearer " + token);
    }
}
