package pl.lasota.sensor.flows.nodes.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.flows.nodes.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.nodes.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JsonToFlows {
    private final NodeCreatorFactory ncf;
    Map<String, RawNode> nodesRaw = new HashMap<>();

    public Node flows(String flowsJson) throws JsonProcessingException {
        NodeCreatorFactory.Factory factory = ncf.create();
        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(flowsJson);

        jsonNode.iterator().forEachRemaining(node -> {
            String ref = ref(node);
            String name = name(node);
            List<String> childed = childed(node);

            new RawNode()
            System.out.println(ref);
            System.out.println(name);
            System.out.println(childed);
        });


        return null;
    }

    private List<String> childed(JsonNode node) {
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


    public class RawNode {
        public RawNode(Node thisNode, List<String> childrenRef) {
            this.thisNode = thisNode;
            this.childrenRef = childrenRef;
        }

        public Node thisNode;
        public List<String> childrenRef;

    }
}
