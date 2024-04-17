package pl.lasota.sensor.internal.apis.api.flows;

import java.io.Serializable;

public record FlowI(Long id, String name, boolean isActivate, String config)  implements Serializable {
}
