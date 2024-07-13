package pl.lasota.sensor.flow.services.nodes.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.NodeStart;

import java.util.Map;
import java.util.Optional;

@Slf4j
public final class NodeUtils {

    public static String GLOBAL_CONTEXT_NAME = "g_c";
    public static String FLOW_CONTEXT_NAME = "f_c";
    public static String LOCAL_CONTEXT_NAME = "l_c";
    public static String RESULT_NAME = "result";


    public static void updateLangContext(LanguageId languageId, LocalContext localContext, FlowContext flowContext, GlobalContext globalContext, Context context) {
        context.getBindings(languageId.getLang()).putMember(LOCAL_CONTEXT_NAME, localContext.getVariables());
        context.getBindings(languageId.getLang()).putMember(FLOW_CONTEXT_NAME, flowContext.getVariables());
        context.getBindings(languageId.getLang()).putMember(GLOBAL_CONTEXT_NAME, globalContext.getVariables());
    }

    public static void updateFlowContext(LanguageId languageId, Context context, LocalContext localContext, FlowContext flowContext, GlobalContext globalContext) {
        getDataContext(languageId, context, localContext.getVariables(), LOCAL_CONTEXT_NAME);
        getDataContext(languageId, context, flowContext.getVariables(), FLOW_CONTEXT_NAME);
        getDataContext(languageId, context, globalContext.getVariables(), GLOBAL_CONTEXT_NAME);

    }

    private static void getDataContext(LanguageId languageId, Context context, Map<String, Object> variable, String member) {
        Value globalValue = context.getBindings(languageId.getLang()).getMember(member);
        if (globalValue != null && !globalValue.isNull() && globalValue.hasMembers()) {
            variable.putAll(globalValue.as(Map.class));
        }
    }

    public static Context buildContext(LanguageId languageId) {
        return Context.newBuilder()
                .allowCreateThread(false)
                .allowCreateProcess(false)
                .allowNativeAccess(false)
                .allowValueSharing(false)
                .allowInnerContextOptions(false)
                .allowExperimentalOptions(false)
                .allowHostClassLoading(false)
                .allowAllAccess(false)
                .allowIO(IOAccess.NONE)
                .sandbox(SandboxPolicy.TRUSTED)
                .allowPolyglotAccess(PolyglotAccess.NONE)
                .allowEnvironmentAccess(EnvironmentAccess.NONE)
                .allowHostAccess(HostAccess.newBuilder().allowMapAccess(true).build())
                .build();
    }

    public static <T> Optional<T> getValue(String pathValue, LocalContext localContext, FlowContext flowContext, GlobalContext globalContext, Class<?> longClass) {
        ObjectMapper mapper = new ObjectMapper();
        if (pathValue == null || pathValue.isBlank()) {
            return Optional.empty();
        }
        try {
            JsonNode rootNode;
            if (pathValue.startsWith(GLOBAL_CONTEXT_NAME)) {
                rootNode = mapper.valueToTree(globalContext.getVariables());
            } else if (pathValue.startsWith(FLOW_CONTEXT_NAME)) {
                rootNode = mapper.valueToTree(flowContext.getVariables());
            } else if (pathValue.startsWith(LOCAL_CONTEXT_NAME)) {
                rootNode = mapper.valueToTree(localContext.getVariables());
            } else {
                return Optional.empty();
            }

            String jsonPath = pathValue.substring(3).replaceAll("\\.", "/");
            JsonNode valueNode = rootNode.at(jsonPath);
            if (valueNode.isMissingNode()) {
                return Optional.empty();
            }
            return Optional.ofNullable(mapper.treeToValue(valueNode, (Class<T>) longClass));
        } catch (Exception e) {
            log.error("Problem with cast ", e);
            return Optional.empty();
        }
    }


    public static boolean isRoot(Node node) {
        return node instanceof NodeStart;
    }

    @Getter
    public enum LanguageId {
        JS("js");
        private final String lang;

        LanguageId(String lang) {
            this.lang = lang;
        }
    }

}
