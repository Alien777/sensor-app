package pl.lasota.sensor.flows.nodes.utils;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GlobalContext {
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final Map<String, ScheduledFuture<?>> schedules = new ConcurrentHashMap<>();
    private final Map<String, Thread> threads = new ConcurrentHashMap<>();
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
