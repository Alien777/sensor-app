package pl.lasota.sensor.flow.model;

import java.io.Serializable;

public record FlowSaveI(Long id, String config, String name) implements Serializable {
}
