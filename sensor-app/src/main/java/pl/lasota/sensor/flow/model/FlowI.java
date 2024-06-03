package pl.lasota.sensor.flow.model;

import java.io.Serializable;

public record FlowI(Long id, String name, boolean isActivate, String config)  implements Serializable {
}
