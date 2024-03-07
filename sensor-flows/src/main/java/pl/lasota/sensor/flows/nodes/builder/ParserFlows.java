package pl.lasota.sensor.flows.nodes.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceConfigException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceException;
import pl.lasota.sensor.core.exceptions.NotFoundPinException;
import pl.lasota.sensor.flows.nodes.nodes.Node;
import pl.lasota.sensor.flows.nodes.nodes.threads.AsyncNode;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserFlows {

    public Node flows(String flowsJson, NodeCreatorFactory.Factory factory) throws JsonProcessingException, NotFoundPinException, NotFoundDeviceConfigException, NotFoundDeviceException {
        log.info("Parse flow to node {} ", flowsJson);
        final Map<String, RawNode> nodesRaw = new HashMap<>();
        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(flowsJson);
        for (JsonNode node : jsonNode) {
            String ref = ref(node);
            String name = name(node);
            boolean isRoot = isRoot(node);
            List<String> childed = childed(node);

            Node n = parseNode(name, node, factory);
            RawNode rawNode = new RawNode(n, childed, ref, isRoot);
            nodesRaw.put(ref, rawNode);
        }


        return createStructured(nodesRaw);
    }

    private Node createStructured(Map<String, RawNode> nodesRaw) {

        List<Map.Entry<String, RawNode>> roots = nodesRaw.entrySet().stream().filter(n -> n.getValue().isRoot).toList();
        if (roots.size() != 1) {
            return null;
        }
        Map.Entry<String, RawNode> root = roots.getFirst();
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

                currentProcess.addFirst(childNodeRaw);
            }
        }

        return root.getValue().node;
    }

    private Node parseNode(String name, JsonNode node, NodeCreatorFactory.Factory factory) throws NotFoundPinException, NotFoundDeviceConfigException, JsonProcessingException, NotFoundDeviceException {
        switch (name) {
            case "SendPwmValueNode" -> {
                String deviceId = fString(node, "deviceKey");
                String memberId = fString(node, "memberKey");
                Integer pin = fInteger(node, "pin");
                String valueKey = fString(node, "valueVariableName");
                return factory.sendPwmValueNode(memberId, deviceId, pin, valueKey);
            }
            case "VariableNode" -> {
                Boolean byCreateInitialization = fBoolean(node, "byCreateInitialization");
                Map<String, Object> variables = fMap(node, "variables");
                return factory.variableNode(byCreateInitialization, variables);
            }
            case "ListeningSensorNode" -> {
                String deviceId = fString(node, "deviceKey");
                String memberId = fString(node, "memberKey");
                return factory.listeningSensorNode(memberId, deviceId);
            }
            case "ExecuteIfNode" -> {
                String condition = fString(node, "condition");
                return factory.executeIfNode(condition);
            }
            case "ExecuteCodeNode" -> {
                String condition = fString(node, "code");
                return factory.executeCodeNode(condition);
            }
            case "AsyncNode" -> {
                return factory.asyncNodeCreator();
            }
            case "CronNode" -> {
                String cron = fString(node, "cron");
                String timesExecuteVariableName = fString(node, "timesExecuteVariableName");
                return factory.cronNode(cron, timesExecuteVariableName);
            }
            case "SleepNode" -> {
                Long sleepTimeSeconds = fLong(node, "sleepTimeSeconds");
                return factory.sleepNode(sleepTimeSeconds);
            }
            default -> throw new IllegalStateException("Unexpected value: " + name);
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
        node.findValue("_childed").iterator().forEachRemaining(ref -> children.add(ref.asText()));
        return children;
    }

    private static String ref(JsonNode node) {
        return node.findValue("_ref").asText();
    }

    private static String name(JsonNode node) {
        return node.findValue("_name").asText();
    }

    private static Boolean isRoot(JsonNode node) {
        try {
            return node.findValue("_root").asBoolean(false);
        } catch (Exception e) {
            return false;
        }

    }

    private static String fString(JsonNode node, String value) {
        return node.findValue(value).asText();
    }

    private static Integer fInteger(JsonNode node, String value) {
        return node.findValue(value).asInt();
    }

    private static Boolean fBoolean(JsonNode node, String value) {
        return node.findValue(value).asBoolean(false);
    }

    private static Map<String, Object> fMap(JsonNode node, String value) {
        JsonNode valueNode = node.findValue(value);
        if (valueNode == null) {
            return null;
        }
        Map<String, Object> resultMap = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = valueNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode currentNode = entry.getValue();
            String key = entry.getKey();
            Object convertedValue = jsonNodeToObject(currentNode);
            resultMap.put(key, convertedValue);
        }
        return resultMap;
    }

    private static Long fLong(JsonNode node, String value) {
        return node.findValue(value).asLong();
    }

    private static Object jsonNodeToObject(JsonNode jsonNode) {
        if (jsonNode.isTextual()) {
            return jsonNode.textValue();
        } else if (jsonNode.isInt()) {
            return jsonNode.intValue();
        } else if (jsonNode.isLong()) {
            return jsonNode.longValue();
        } else if (jsonNode.isDouble()) {
            return jsonNode.doubleValue();
        } else if (jsonNode.isBoolean()) {
            return jsonNode.booleanValue();
        } else {
            return jsonNode.toString(); // Domyślnie zwróć jako tekst
        }
    }


    private static class RawNode {
        public RawNode(Node thisNode, List<String> childrenRef, String ref, boolean isRoot) {
            this.node = thisNode;
            this.childrenRef = childrenRef;
            this.ref = ref;
            this.isRoot = isRoot;
        }

        public boolean isRoot;
        public String ref;
        public Node node;
        public List<String> childrenRef;
    }


}
