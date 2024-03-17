package pl.lasota.sensor.gui.model;

import lombok.Data;
import pl.lasota.sensor.core.models.flows.Flow;

@Data
public class FlowT {
    private Long id;
    private String name;
    private boolean isActivate;
    private String config;

    public static FlowT map(Flow flow) {

        FlowT flowT = new FlowT();
        flowT.id = flow.getId();
        flowT.name = flow.getName();
        flowT.isActivate = flow.isActivate();
        flowT.config = flow.getConfig();
        return flowT;

    }
}
