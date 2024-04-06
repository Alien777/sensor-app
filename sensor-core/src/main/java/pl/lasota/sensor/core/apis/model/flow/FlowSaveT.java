package pl.lasota.sensor.core.apis.model.flow;

import lombok.Data;
import pl.lasota.sensor.core.entities.flows.Flow;


@Data
public class FlowSaveT   {
    private Long id;
    private String config;
    private String name;
    public Flow map() {
        Flow flow = new Flow();
        flow.setId(id);
        flow.setConfig(config);
        flow.setName(name);
        return flow;
    }

}
