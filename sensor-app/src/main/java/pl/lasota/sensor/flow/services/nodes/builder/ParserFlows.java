package pl.lasota.sensor.flow.services.nodes.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.nodes.AsyncNode;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.configs.properties.FlowsProperties;
import pl.lasota.sensor.flow.services.nodes.utils.NodeUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserFlows {

    private final FlowsProperties flowsProperties;
    private final ApplicationContext applicationContext;

    public List<Node> flows(String flowsJson, GlobalContext globalContext) {
        try {
            final Map<String, RawNode> nodesRaw = new HashMap<>();
            ObjectMapper om = new ObjectMapper();
            JsonNode jsonNode = om.readTree(flowsJson);
            JsonNode nodes = jsonNode.findValue("nodes");
            for (JsonNode node : nodes) {
                String ref = ref(node);
                String name = name(node);
                List<String> childed = childed(node);
                Node n = parseNode(ref, name, node, globalContext);
                RawNode rawNode = new RawNode(n, childed, ref);
                nodesRaw.put(ref, rawNode);
            }
            return createStructured(nodesRaw);
        } catch (JsonProcessingException e) {
            throw new SensorFlowException(e, "Occurred problem with parsing flow");
        }
    }

    public static String fString(JsonNode node, String value) {
        return node.findValue(value).asText();
    }

    public static Integer fInteger(JsonNode node, String value) {
        return node.findValue(value).asInt();
    }

    public static Long fLong(JsonNode node, String value) {
        return node.findValue(value).asLong();
    }

    private List<Node> createStructured(Map<String, RawNode> nodesRaw) {

        List<Map.Entry<String, RawNode>> roots = nodesRaw.entrySet().stream().filter(n -> NodeUtils.isRoot(n.getValue().node)).toList();
        Set<String> created = new HashSet<>();
        List<Node> collect = roots.stream().map(root -> {

            RawNode rootRaw = root.getValue();
            FlowsBuilder fb = FlowsBuilder.root(rootRaw.node);

            LinkedList<RawNode> currentProcess = new LinkedList<>();
            currentProcess.add(rootRaw);
            while (!currentProcess.isEmpty()) {
                RawNode node = currentProcess.poll();
                String parentRef = node.ref;
                log.info("Process node: {}, {} ", parentRef, node.node.getClass().getSimpleName());
                for (String childRef : node.childrenRef) {
                    RawNode childNodeRaw = nodesRaw.get(childRef);
                    RawNode parentNodeRaw = nodesRaw.get(parentRef);
                    log.info("Parent is: {}, {} and has child: {}, {} ", parentNodeRaw.ref, parentNodeRaw.node.getClass().getSimpleName(),
                            childNodeRaw.ref, childNodeRaw.node.getClass().getSimpleName());
                    addNode(parentNodeRaw.node, childNodeRaw.node, fb);
                    if (!created.contains(parentNodeRaw.ref)) {
                        currentProcess.addFirst(childNodeRaw);
                        created.add(parentNodeRaw.ref);
                    }
                }
            }
            return root.getValue().node;
        }).collect(Collectors.toList());

        return collect;
    }


    private Node parseNode(String ref, String name, JsonNode root, GlobalContext globalContext) {
        JsonNode node = root.get("sensor");
        Node createdNode;
        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(flowsProperties.getScanNodes())
                .enableAnnotationInfo()// Scan only the specified package
                .scan()) {

            createdNode = scanResult.getClassesWithAnnotation(FlowNode.class.getName())
                    .filter(classInfo -> classInfo.getSimpleName().equals(name))
                    .stream()
                    .findFirst()
                    .map(classInfo -> {
                        try {
                            Class<?> clazz = Class.forName(classInfo.getName());
                            return (Node) clazz.getMethod("create", String.class, GlobalContext.class, JsonNode.class, ApplicationContext.class)
                                    .invoke(null, ref, globalContext, node, applicationContext);
                        } catch (Exception e) {
                            throw new SensorFlowException(e, "Problem with create node {}", name);
                        }
                    })
                    .orElse(null);
            if (createdNode == null) {
                throw new SensorFlowException("Not found node by {}", name);
            }
            return createdNode;

        }
    }

    private void addNode(Node parent, Node child, FlowsBuilder fb) {
        if (child instanceof AsyncNode) {
            fb.addFirst(parent, child);
        } else {
            fb.add(parent, child);
        }
    }


    private static List<String> childed(JsonNode node) {
        List<String> children = new ArrayList<>();
        node.findValue("childed").iterator().forEachRemaining(ref -> children.add(ref.asText()));
        return children;
    }

    private static String ref(JsonNode node) {
        return node.findValue("ref").asText();
    }

    private static String name(JsonNode node) {
        return node.findValue("name").asText();
    }

    private static class RawNode {
        public RawNode(Node thisNode, List<String> childrenRef, String ref) {
            this.node = thisNode;
            this.childrenRef = childrenRef;
            this.ref = ref;
        }

        public String ref;
        public Node node;
        public List<String> childrenRef;
    }
}
