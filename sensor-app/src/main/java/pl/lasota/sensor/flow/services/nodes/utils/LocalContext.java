package pl.lasota.sensor.flow.services.nodes.utils;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class LocalContext {
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
}
