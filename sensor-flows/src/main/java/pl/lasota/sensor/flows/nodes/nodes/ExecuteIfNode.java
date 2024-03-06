package pl.lasota.sensor.flows.nodes.nodes;

import org.graalvm.polyglot.Context;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

import java.util.Map;


@FlowNode
public class ExecuteIfNode extends Node {

    private final String condition;

    public ExecuteIfNode(PrivateContext privateContext, String condition) {
        super(privateContext);
        this.condition = condition;
    }

    @Override
    public void execute() throws Exception {
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {

            for (Map.Entry<String, Object> entry : privateContext.getVariables().entrySet()) {
                context.getBindings("js").putMember(entry.getKey(), entry.getValue());
            }

            String codeToExecute = String.format("%s ? true : false;", condition);
            boolean result = context.eval("js", codeToExecute).asBoolean();
            if (result) {
                super.execute();
            }
        }
    }
}
