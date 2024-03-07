package pl.lasota.sensor.flows.nodes.nodes;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

import java.util.Map;


@FlowNode
public class ExecuteCodeNode extends Node {

    private final String code;

    public ExecuteCodeNode(PrivateContext privateContext, String code) {
        super(privateContext);
        this.code = code;
    }

    @Override
    public void execute() throws Exception {
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {

            for (Map.Entry<String, Object> entry : privateContext.getVariables().entrySet()) {
                context.getBindings("js").putMember(entry.getKey(), entry.getValue());
            }

            String codeToExecute = String.format("%s", code);
            Context result = context.eval("js", codeToExecute).getContext();
            privateContext.getVariables().forEach((s, o) -> {
                Value value = result.getBindings("js").getMember(s);
                if (value != null) {
                    Object javaValue = value.as(Object.class);
                    privateContext.getVariables().put(s, javaValue);
                }
            });

            super.execute();
        }
    }
}
