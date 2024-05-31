package pl.lasota.sensor.flows.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@FlowNode
public class VoiceFireCommendNode extends  FireOnceNode {

    private final List<String> commends;

    public VoiceFireCommendNode(String id, GlobalContext globalContext, List<String> commends) {
        super(id, globalContext);
        this.commends = commends;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String commends1 = node.asText("commends");
        List<String> commends = new ArrayList<>(Arrays.asList(commends1.split(";")));
        return new VoiceFireCommendNode(ref, globalContext, commends);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public boolean start() {
        LocalContext localContext = new LocalContext();
        super.execute(localContext);
        return true;
    }
}
