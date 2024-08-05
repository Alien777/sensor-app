package pl.lasota.sensor.flow.services.nodes.send;

import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.bus.WaitForResponseInputStreamBus;
import pl.lasota.sensor.flow.services.nodes.AsyncNodeConsumer;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SendAndWait implements AsyncNodeConsumer<String, String> {
    private final WaitForResponseInputStreamBus waitForResponseInputStreamBus;
    private final ProvideRequest provideRequest;
    private final CountDownLatch latch;
    private UUID waitForMessage;

    public static SendAndWait of(WaitForResponseInputStreamBus waitForResponseInputStreamBus, ProvideRequest provideRequest) {
        return new SendAndWait(waitForResponseInputStreamBus, provideRequest);
    }

    private SendAndWait(WaitForResponseInputStreamBus waitForResponseInputStreamBus, ProvideRequest provideRequest) {
        this.waitForResponseInputStreamBus = waitForResponseInputStreamBus;
        this.provideRequest = provideRequest;
        waitForResponseInputStreamBus.addConsumer(this);
        latch = new CountDownLatch(1);

    }

    public boolean send() {
        try {
            waitForMessage = provideRequest.send();
            return latch.await(1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Error while sending provide request [Send And Wait]", e);
            return false;
        } finally {
            waitForResponseInputStreamBus.removeConsumer(this);
        }
    }

    @Override
    public void consume(String s, String requestId) {
        if (requestId.equals(waitForMessage.toString())) {
            latch.countDown();
        }
    }
}
