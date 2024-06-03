package pl.lasota.sensor.device.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record ConfigI(Long id, String config, String forVersion, OffsetDateTime time) implements Serializable {
}
