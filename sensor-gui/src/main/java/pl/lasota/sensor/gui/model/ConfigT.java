package pl.lasota.sensor.gui.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import pl.lasota.sensor.core.models.device.DeviceConfig;

import java.time.OffsetDateTime;

@Data
public class ConfigT {
    private Long id;
    private String schema;
    private String config;
    private String forVersion;
    private OffsetDateTime time;

    public static ConfigT map(DeviceConfig dc, String schema) {
        ConfigT configT = new ConfigT();
        configT.id = dc.getId();
        configT.config = dc.getConfig();
        configT.forVersion = dc.getForVersion();
        configT.time = dc.getTime();
        configT.schema = schema;
        return configT;
    }
}
