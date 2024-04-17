package pl.lasota.sensor.internal.apis.api.flows;

import java.io.Serializable;

public record FlowSaveI(Long id, String config, String name) implements Serializable {
}
