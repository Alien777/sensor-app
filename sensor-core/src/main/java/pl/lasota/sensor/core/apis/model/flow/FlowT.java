package pl.lasota.sensor.core.apis.model.flow;

import lombok.Data;
import pl.lasota.sensor.core.entities.flows.Flow;

@Data
public class FlowT {
    private Long id;
    private String name;
    private boolean isActivate;
    private String config;
    private String serverIp;
    private String serverId;

    public static FlowT map(Flow flow) {

        FlowT flowT = new FlowT();
        flowT.id = flow.getId();
        flowT.name = flow.getName();
        flowT.isActivate = flow.isActivate();
        flowT.config = flow.getConfig();
        flowT.serverIp = flow.getServerIp();
        flowT.serverId = flow.getServerId();
        return flowT;

    }
}
