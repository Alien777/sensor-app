package pl.lasota.sensor.flow.services.nodes.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class KeySensor {
    private final String deviceId;
    private final String nodeId;
}
