package pl.lasota.sensor.gateway.gui.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ConfigSaveT {

    @NotEmpty
    @Length(min = 10, max = 2000)
    private String config;

    @NotEmpty
    @Length(min = 2, max = 10)
    private String version;
}
