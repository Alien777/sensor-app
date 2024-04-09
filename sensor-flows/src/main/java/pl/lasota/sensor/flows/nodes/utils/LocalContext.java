package pl.lasota.sensor.flows.nodes.utils;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Getter
public class LocalContext {
    private final Map<String, Object> variables = new ConcurrentHashMap<>();
}
