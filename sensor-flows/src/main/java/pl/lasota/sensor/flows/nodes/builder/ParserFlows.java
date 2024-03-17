package pl.lasota.sensor.flows.nodes.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.exceptions.FlowException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceConfigException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceException;
import pl.lasota.sensor.core.exceptions.NotFoundPinException;
import pl.lasota.sensor.core.models.mqtt.payload.MessageType;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.nodes.AsyncNode;
import pl.lasota.sensor.flows.nodes.nodes.SendPwmValueNode;
import pl.lasota.sensor.flows.nodes.nodes.ListeningSensorNode;

import java.util.*;
import java.util.stream.Collectors;

import static pl.lasota.sensor.flows.nodes.utils.NodeUtils.isRoot;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserFlows {

    public List<Node> flows(String flowsJson, NodeCreatorFactory.Factory factory) throws JsonProcessingException, NotFoundPinException, NotFoundDeviceConfigException, NotFoundDeviceException, FlowException {
        log.info("Parse flow to node {} ", flowsJson);
        final Map<String, RawNode> nodesRaw = new HashMap<>();
        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(flowsJson);
        for (JsonNode node : jsonNode) {
            String ref = ref(node);
            String name = name(node);
            List<String> childed = childed(node);
            Node n = parseNode(ref, name, node, factory);
            RawNode rawNode = new RawNode(n, childed, ref);
            nodesRaw.put(ref, rawNode);
        }
        return createStructured(nodesRaw);
    }

    private List<Node> createStructured(Map<String, RawNode> nodesRaw) {

        List<Map.Entry<String, RawNode>> roots = nodesRaw.entrySet().stream().filter(n -> isRoot(n.getValue().node)).toList();

        return roots.stream().map(root -> {

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
        }).collect(Collectors.toList());
    }

    private Node parseNode(String id, String name, JsonNode root, NodeCreatorFactory.Factory factory) throws NotFoundPinException, NotFoundDeviceConfigException, JsonProcessingException, NotFoundDeviceException, FlowException {
        JsonNode node = root.get("sensor");
        switch (name) {
            case "SendPwmValueNode" -> {
                String deviceId = fString(node, "deviceId");
                String memberId = fString(node, "memberKey");
                Integer pin = fInteger(node, "pin");
                String valueKey = fString(node, "valueVariable");
                return factory.sendPwmValueNode(id, SendPwmValueNode.Data.create(memberId, deviceId, valueKey, pin));
            }
            case "ListeningSensorNode" -> {
                String deviceId = fString(node, "deviceId");
                String memberId = fString(node, "memberKey");
                MessageType type = MessageType.valueOf(fString(node, "messageType").toUpperCase());
                ListeningSensorNode.Data data = ListeningSensorNode.Data.create(deviceId, type);
                return factory.listeningSensorNode(id, memberId, data);
            }
            case "ExecuteCodeNode" -> {
                String condition = fString(node, "code");
                return factory.executeCodeNode(id, condition);
            }
            case "AsyncNode" -> {
                return factory.asyncNodeCreator(id);
            }
            case "CronNode" -> {
                String cron = fString(node, "cron");
                return factory.cronNode(id, cron);
            }
            case "SleepNode" -> {
                Long sleepTimeSeconds = fLong(node, "sleepTimeSeconds");
                return factory.sleepNode(id, sleepTimeSeconds);
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

    private static String fString(JsonNode node, String value) {
        return node.findValue(value).asText();
    }

    private static Integer fInteger(JsonNode node, String value) {
        return node.findValue(value).asInt();
    }

    private static Long fLong(JsonNode node, String value) {
        return node.findValue(value).asLong();
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
