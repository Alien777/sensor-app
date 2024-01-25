package pl.lasota.sensor.api.mqtt.filter;

import lombok.Data;

@Data
public class Context {
    private boolean shouldSendConfig = false;
}
