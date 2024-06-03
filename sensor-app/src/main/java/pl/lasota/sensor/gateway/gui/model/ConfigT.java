package pl.lasota.sensor.gateway.gui.model;

import lombok.Data;
import pl.lasota.sensor.device.model.ConfigI;

import java.time.OffsetDateTime;

@Data
public class ConfigT {
    private Long id;
    private String schema;
    private String config;
    private String forVersion;
    private OffsetDateTime time;

    public static ConfigT map(ConfigI dc, String schema) {
        ConfigT configT = new ConfigT();
        configT.id = dc.id();
        configT.config = dc.config();
        configT.forVersion = dc.forVersion();
        configT.time = dc.time();
        configT.schema = schema;
        return configT;
    }
}
