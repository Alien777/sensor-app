package pl.lasota.sensor.bus;

import org.junit.jupiter.api.Test;
import pl.lasota.sensor.flow.model.FlowSensorI;
import pl.lasota.sensor.payload.PayloadType;
import pl.lasota.sensor.payload.message.ConnectedAck;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FlowSensorICustomerBroadcastTest {


    @Test
    void testPerformance() throws Exception {
        final int recordCount = 2000;
        FlowSensorIInputStreamBus flowSensorIInputStreamBus = new FlowSensorIInputStreamBus();
        CountDownLatch latch = new CountDownLatch(recordCount);
        AtomicInteger counter = new AtomicInteger(0);

        flowSensorIInputStreamBus.addConsumer((s, d) -> {
            latch.countDown();
            counter.incrementAndGet();
        });

        long startTime = System.nanoTime();

        for (int i = 0; i < recordCount; i++) {
            FlowSensorI flowSensorI = FlowSensorI.of(PayloadType.CONNECTED_ACK, "m", "d", UUID.randomUUID(), ConnectedAck.of());
            flowSensorIInputStreamBus.takeBroadcaster("streamInfo").write(flowSensorI);
        }

        latch.await(100, TimeUnit.MILLISECONDS);

        long endTime = System.nanoTime();
        long durationInMillis = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        assertTrue(durationInMillis < 2000, "Performance test failed: took longer than expected");
        assertEquals(counter.get(), recordCount);
    }
}
