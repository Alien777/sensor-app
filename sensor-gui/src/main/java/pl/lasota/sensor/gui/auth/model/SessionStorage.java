package pl.lasota.sensor.gui.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class SessionStorage {

    public User user;
    public String token;

    public void clear() {
        token = null;
        user = null;
    }

    public boolean isEmpty() {
        return user == null || token == null;
    }

}
