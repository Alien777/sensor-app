package pl.lasota.sensor.flows.nodes;

import lombok.RequiredArgsConstructor;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

import java.time.Duration;

@RequiredArgsConstructor
public class WaitNode extends Node {

    private final String waitForThread;
    private final long waitTimeSecond;

    @Override
    public void execute(PrivateContext privateContext) throws Exception {
        Thread thread = privateContext.getThreads().get(waitForThread);
        if (thread != null) {
            thread.join(Duration.ofSeconds(waitTimeSecond));
        }
        super.execute(privateContext);
    }

}
