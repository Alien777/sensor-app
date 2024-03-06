package pl.lasota.sensor.flows.nodes.nodes;

import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

import java.util.Map;

@FlowNode
public class VariableNode extends Node {

    private final boolean fastInitialization;
    private final Map<String, Object> variable;

    public VariableNode(PrivateContext privateContext, boolean fastInitialization, Map<String, Object> variable) {
        super(privateContext);
        this.fastInitialization = fastInitialization;
        this.variable = variable;
        execute(privateContext, fastInitialization, variable);
    }

    @Override
    public void execute() throws Exception {
        execute(privateContext, !fastInitialization, variable);
        super.execute();
    }

    @Override
    public void clear() {
        variable.forEach((s, o) -> privateContext.getVariables().remove(s));
        super.clear();
    }

    private static void execute(PrivateContext privateContext, boolean fastInitialization, Map<String, Object> variable) {
        if (fastInitialization) {
            variable.forEach((s, o) -> privateContext.getVariables().put(s, o));
        }
    }
}
