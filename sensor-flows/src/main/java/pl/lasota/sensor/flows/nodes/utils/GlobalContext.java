package pl.lasota.sensor.flows.nodes.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.lasota.sensor.member.User;

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
    private final User user;


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
                user.getGrantedRoles());
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
