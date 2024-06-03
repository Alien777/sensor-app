package pl.lasota.sensor.flow.services.nodes;

import pl.lasota.sensor.flow.services.nodes.utils.FlowContext;

public interface StartFlowNode {

    void start(FlowContext flowContext) throws Exception;

}
