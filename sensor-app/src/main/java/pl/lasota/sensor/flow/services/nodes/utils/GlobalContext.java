package pl.lasota.sensor.flow.services.nodes.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.lasota.sensor.entities.Member;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@RequiredArgsConstructor
public class GlobalContext {
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final Map<String, ScheduledFuture<?>> schedules = new ConcurrentHashMap<>();
    private final Map<String, Thread> threads = new ConcurrentHashMap<>();
    private final Map<String, Object> variables = new ConcurrentHashMap<>();
    private final Member user;


    public final Object getVariable(String key) {
        return variables.get(key);
    }

    public final Object getVariable(String key, Object def) {
        if (key == null) {
            return def;
        }
        return getVariable(key) == null ? def : getVariable(key);
    }

    public void stopFlow() {
        stopped.set(true);
    }

    public boolean isStopped() {
        return stopped.get();
    }

    public void recreateSecurityHolder()
    {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                null,
                user.getAuthorities());
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
