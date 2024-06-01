package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;
import pl.lasota.sensor.flow.services.nodes.utils.NodeUtils;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;


@FlowNode
public class ExecuteCodeNode extends Node {

    private final String code;

    private ExecuteCodeNode(String id, GlobalContext globalContext, String code) {
        super(id, globalContext);
        this.code = code;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String cron = fString(node, "code");
        return new ExecuteCodeNode(ref, globalContext, cron);
    }

    @Override
    public void execute(LocalContext localContext) {
        try (Context context = NodeUtils.buildContext(NodeUtils.LanguageId.JS)) {
            NodeUtils.updateLangContext(NodeUtils.LanguageId.JS, localContext, globalContext, context);
            String codeToExecute = String.format("%s", code);
            Context c = context.eval(NodeUtils.LanguageId.JS.getLang(), codeToExecute).getContext();
            Value result = c.getBindings(NodeUtils.LanguageId.JS.getLang()).getMember(NodeUtils.RESULT_NAME);
            NodeUtils.updateFlowContext(NodeUtils.LanguageId.JS, c, localContext, globalContext);
            if (result != null && result.isBoolean() && result.asBoolean()) {
                super.execute(localContext);
            }
        }
    }
}
