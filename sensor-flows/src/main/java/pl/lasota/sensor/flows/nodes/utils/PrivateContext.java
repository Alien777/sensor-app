package pl.lasota.sensor.flows.nodes.utils;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Getter
public class PrivateContext {
    private final Map<String, ScheduledFuture<?>> schedules = new HashMap<>();
    private final Map<String, Thread> threads = new HashMap<>();
    private final Map<String, Object> variables = new HashMap<>();
}
