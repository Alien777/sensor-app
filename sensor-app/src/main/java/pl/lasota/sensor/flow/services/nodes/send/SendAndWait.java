package pl.lasota.sensor.flow.services.nodes.send;

import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.bus.FlowSensorIInputStreamBus;
import pl.lasota.sensor.flow.model.FlowSensorI;
import pl.lasota.sensor.flow.services.nodes.AsyncNodeConsumer;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SendAndWait implements AsyncNodeConsumer<String, FlowSensorI> {
    private final FlowSensorIInputStreamBus flowSensorIInputStreamBus;
    private final ProvideRequest provideRequest;
    private final CountDownLatch latch;
    private UUID waitForMessage;
    private final long start = System.currentTimeMillis();

    public static SendAndWait of(FlowSensorIInputStreamBus flowSensorIInputStreamBus, ProvideRequest provideRequest) {
        return new SendAndWait(flowSensorIInputStreamBus, provideRequest);
    }

    private SendAndWait(FlowSensorIInputStreamBus flowSensorIInputStreamBus, ProvideRequest provideRequest) {
        this.flowSensorIInputStreamBus = flowSensorIInputStreamBus;
        this.provideRequest = provideRequest;
        flowSensorIInputStreamBus.addConsumer(this);
        latch = new CountDownLatch(1);
    }

    public boolean send() {
        try {
            waitForMessage = provideRequest.send();
            return latch.await(3000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Error while sending provide request [Send And Wait]", e);
            return false;
        } finally {
            flowSensorIInputStreamBus.removeConsumer(this);
            log.info("Time to send request {} == {}", waitForMessage, System.currentTimeMillis() - start);
        }
    }

    @Override
    public void consume(String s, FlowSensorI requestId) {
        if (requestId.getRequestId().equals(waitForMessage)) {
            latch.countDown();
        }
    }

    @Override
    public void error(Exception e) {
        log.error("Error while sending provide request [Send And Wait]", e);
    }
}
