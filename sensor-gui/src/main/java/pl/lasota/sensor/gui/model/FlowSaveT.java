
package pl.lasota.sensor.gui.model;

import lombok.Data;
import pl.lasota.sensor.core.models.flows.Flow;

@Data
public class FlowSaveT {
    private Long id;
    private String name;
    private String config;

    public static Flow map(FlowSaveT flow) {
        Flow f = new Flow();
        f.setId(flow.getId());
        f.setConfig(flow.getConfig());
        f.setName(flow.getName());
        return f;

    }
}
