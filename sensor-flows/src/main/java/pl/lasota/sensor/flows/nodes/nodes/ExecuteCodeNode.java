package pl.lasota.sensor.flows.nodes.nodes;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

import static pl.lasota.sensor.flows.nodes.utils.NodeUtils.*;


@FlowNode
public class ExecuteCodeNode extends Node {

    private final String code;

    public ExecuteCodeNode(String id, GlobalContext globalContext, String code) {
        super(id, globalContext);
        this.code = code;
    }

    @Override
    public void execute(LocalContext localContext) {
        try (Context context = buildContext(LanguageId.JS)) {
            updateLangContext(LanguageId.JS, localContext, globalContext, context);
            String codeToExecute = String.format("%s", code);
            Context c = context.eval(LanguageId.JS.getLang(), codeToExecute).getContext();
            Value result = c.getBindings(LanguageId.JS.getLang()).getMember(RESULT_NAME);
            updateFlowContext(LanguageId.JS, c, localContext, globalContext);
            if (result != null && result.isBoolean() && result.asBoolean()) {
                super.execute(localContext);
            }
        }
    }
}
