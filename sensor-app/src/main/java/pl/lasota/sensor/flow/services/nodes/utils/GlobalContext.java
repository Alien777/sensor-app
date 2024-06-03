package pl.lasota.sensor.flow.services.nodes.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@RequiredArgsConstructor
public class GlobalContext {
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final Map<String, Object> variables = new ConcurrentHashMap<>();

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

}
