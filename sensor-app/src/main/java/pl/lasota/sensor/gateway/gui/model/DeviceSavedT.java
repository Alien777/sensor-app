package pl.lasota.sensor.gateway.gui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class DeviceSavedT {
    private String token;
    private String server;
}
