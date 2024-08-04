package pl.lasota.sensor.flow.services.nodes.nodes;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.flow.FlowApiInterface;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fLong;

@FlowNode
public class StopCurrentProcessFlowNode extends Node {
    private final Long flowId;
    private final FlowApiInterface flowApiInterface;

    private StopCurrentProcessFlowNode(String id, GlobalContext globalContext, long flowId, FlowApiInterface flowApiInterface) {
        super(id, globalContext);
        this.flowId = flowId;
        this.flowApiInterface = flowApiInterface;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        Long typeOfStopNode = fLong(node, "flowId");
        FlowApiInterface flowApiInterface = context.getBean(FlowApiInterface.class);
        return new StopCurrentProcessFlowNode(ref, globalContext, typeOfStopNode, flowApiInterface);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    protected void fireChildNodes(LocalContext localContext) throws Exception {
        if (flowApiInterface != null) {
            flowApiInterface.restartFlow(flowId);
        }
        super.fireChildNodes(localContext);
    }

}
