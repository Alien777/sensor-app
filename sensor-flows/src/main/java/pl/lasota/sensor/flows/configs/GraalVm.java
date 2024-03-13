package pl.lasota.sensor.flows.configs;

import lombok.Data;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.FileSystem;
import org.graalvm.polyglot.io.IOAccess;
import org.graalvm.polyglot.io.MessageTransport;
import org.graalvm.polyglot.io.ProcessHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Map;

@Component
@ConfigurationProperties("sensor.graalvm")
@Data
public class GraalVm     {
//    private Engine sharedEngine;
    private String[] permittedLanguages;

//    private OutputStream out;
//    private OutputStream err;
//    private InputStream in;
    private Map<String, String> options;
    private Map<String, String[]> arguments;
//    private Predicate<String> hostClassFilter = UNSET_HOST_LOOKUP;
    private Boolean allowNativeAccess;
    private Boolean allowCreateThread;
    private boolean allowAllAccess;
    private Boolean allowIO;
    private Boolean allowHostClassLoading;
    private Boolean allowExperimentalOptions;
    private Boolean allowHostAccess;
    private boolean allowValueSharing = true;
    private Boolean allowInnerContextOptions;
    private PolyglotAccess polyglotAccess;
    private HostAccess hostAccess;
    private IOAccess ioAccess;
    private FileSystem customFileSystem;
    private MessageTransport messageTransport;
    private Object customLogHandler;
    private Boolean allowCreateProcess;
    private ProcessHandler processHandler;
    private EnvironmentAccess environmentAccess;
    private ResourceLimits resourceLimits;
    private Map<String, String> environment;
    private ZoneId zone;
    private Path currentWorkingDirectory;
    private ClassLoader hostClassLoader;
    private boolean useSystemExit;
    private SandboxPolicy sandboxPolicy;
}
