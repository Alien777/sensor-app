package pl.lasota.sensor.flows.nodes.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;

import java.util.Map;
import java.util.Optional;

@Slf4j
public final class NodeUtils {

    public static String GLOBAL_CONTEXT_NAME = "g_c";
    public static String LOCAL_CONTEXT_NAME = "l_c";
    public static String RESULT_NAME = "result";


    public static void updateLangContext(LanguageId languageId, LocalContext localContext, GlobalContext globalContext, Context context) {
        context.getBindings(languageId.getLang()).putMember(LOCAL_CONTEXT_NAME, localContext.getVariables());
        context.getBindings(languageId.getLang()).putMember(GLOBAL_CONTEXT_NAME, globalContext.getVariables());
    }

    public static void updateFlowContext(LanguageId languageId, Context context, LocalContext localContext, GlobalContext globalContext) {
        getDataContext(languageId, context, localContext.getVariables(), LOCAL_CONTEXT_NAME);
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

    public static <T> Optional<T> getValue(String pathValue, LocalContext localContext, GlobalContext globalContext) {
        if (pathValue == null || pathValue.isBlank()) {
            return Optional.empty();
        }
        try {
            var split = pathValue.split("\\.");
            if (split.length == 1) {
                var o = localContext.getVariables().get(pathValue);
                return Optional.ofNullable((T) o);
            } else if (split.length == 2) {
                if (split[0].equals(GLOBAL_CONTEXT_NAME)) {
                    var o = globalContext.getVariables().get(split[1]);
                    return Optional.ofNullable((T) o);
                } else {
                    var o = localContext.getVariables().get(split[1]);
                    return Optional.ofNullable((T) o);
                }
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Problem with cast ", e);
            return Optional.empty();
        }
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
