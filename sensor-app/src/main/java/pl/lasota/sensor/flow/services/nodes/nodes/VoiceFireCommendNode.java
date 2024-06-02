package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.AI;
import pl.lasota.sensor.bus.AudioWaveInputStreamBus;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.StartFlowNode;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@Slf4j
@FlowNode
public class VoiceFireCommendNode extends Node implements StartFlowNode {


    private final List<List<String>> commends;
    private final AudioWaveInputStreamBus audioWaveInputStreamBus;
    private final AI ai = AI.create();
    ;

    public VoiceFireCommendNode(String id, GlobalContext globalContext, List<List<String>> commends, AudioWaveInputStreamBus audioWaveInputStreamBus) {
        super(id, globalContext);
        this.commends = commends;
        this.audioWaveInputStreamBus = audioWaveInputStreamBus;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String commends1 = fString(node, "commends");
        List<String> commends = new ArrayList<>(Arrays.asList(commends1.split(";")));
        AudioWaveInputStreamBus bean = context.getBean(AudioWaveInputStreamBus.class);
        List<List<String>> commendsToken = commends.stream().map(AI::tokenizer).toList();
        return new VoiceFireCommendNode(ref, globalContext, commendsToken, bean);
    }

    private final BiConsumer<Member, String> b = new BiConsumer<>() {
        @Override
        public void accept(Member member, String s) {
            if (globalContext.isRunningRightNow.get()) {
                return;
            }
            globalContext.isRunningRightNow.set(true);
            if (!globalContext.getMember().getId().equals(member.getId())) {
                return;
            }
            boolean b2 = ai.matchText(s, commends);
            if (b2) {
                LocalContext localContext = new LocalContext();
                VoiceFireCommendNode.super.execute(localContext);
            }
            globalContext.isRunningRightNow.set(false);
        }
    };


    @Override
    public void clear() {
        audioWaveInputStreamBus.removeConsumer(b);
        super.clear();
    }

    @Override
    public boolean start() {
        audioWaveInputStreamBus.addConsumer(b);
        return true;
    }
}
