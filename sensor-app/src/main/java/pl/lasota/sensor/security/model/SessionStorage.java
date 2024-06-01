package pl.lasota.sensor.security.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.entities.Member;


@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class SessionStorage {

    public Member member;
    public String token;

    public void clear() {
        token = null;
        member = null;
    }

    public boolean isEmpty() {
        return member == null || token == null;
    }

}
