package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.utils.Tokenizer;
import pl.lasota.sensor.bus.AudioWaveInputStreamBus;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.flow.services.nodes.AsyncNodeConsumer;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.StartFlowNode;
import pl.lasota.sensor.flow.services.nodes.utils.FlowContext;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@Slf4j
@FlowNode
public class VoiceFireCommendNode extends Node implements StartFlowNode, AsyncNodeConsumer<Member, String> {

    private final List<List<String>> commends;
    private final AudioWaveInputStreamBus audioWaveInputStreamBus;
    private final Tokenizer tokenizer = Tokenizer.create();

    public VoiceFireCommendNode(String id, GlobalContext globalContext, List<List<String>> commends, AudioWaveInputStreamBus audioWaveInputStreamBus) {
        super(id, globalContext);
        this.commends = commends;
        this.audioWaveInputStreamBus = audioWaveInputStreamBus;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        List<String> commends = new ArrayList<>(Arrays.asList(fString(node, "commends").split(";")));
        AudioWaveInputStreamBus bean = context.getBean(AudioWaveInputStreamBus.class);
        List<List<String>> commendsToken = commends.stream().map(Tokenizer::tokenizer).toList();
        return new VoiceFireCommendNode(ref, globalContext, commendsToken, bean);
    }


    @Override
    public boolean preConsume(Member member, String s) {
        return flowContext.getMember().getId().equals(member.getId());
    }

    @Override
    public void clear() {
        audioWaveInputStreamBus.removeConsumer(this);
        super.clear();
    }

    @Override
    public void start(FlowContext flowContext) throws Exception {
        super.propagateFlowContext(flowContext);
        audioWaveInputStreamBus.addConsumer(this);
    }

    @Override
    public void error(Exception e) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void consume(Member member, String s) throws Exception {
        if (tokenizer.matchText(s, commends)) {
            LocalContext localContext = new LocalContext();
            VoiceFireCommendNode.super.execute(localContext);
        }
    }
}
