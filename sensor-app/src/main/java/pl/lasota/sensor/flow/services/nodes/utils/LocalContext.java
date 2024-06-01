package pl.lasota.sensor.flow.services.nodes.utils;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class LocalContext {
    private final Map<String, Object> variables = new ConcurrentHashMap<>();
}
