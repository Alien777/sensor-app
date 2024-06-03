package pl.lasota.sensor.bus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.lasota.sensor.flow.model.FlowSensorI;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FlowSensorIInputStreamBusTest {

    @Autowired
    private FlowSensorIInputStreamBus flowSensorIInputStreamBus;

    @BeforeEach
    void setUp() throws IOException {
        // Set up any required resources or configuration
    }

    @Test
    @Disabled
    void testPerformance() throws IOException, InterruptedException {
        final int recordCount = 50000;
        CountDownLatch latch = new CountDownLatch(recordCount);


        flowSensorIInputStreamBus.addConsumer((s, flowSensorI) -> latch.countDown());

        long startTime = System.nanoTime();

        for (int i = 0; i < recordCount; i++) {
            FlowSensorI flowSensorI = new FlowSensorI();

            flowSensorIInputStreamBus.takeBroadcaster("streamInfo").write(objectOutputStream -> {
                try {
                    objectOutputStream.writeObject(flowSensorI);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        latch.await(); // Wait until all records are processed

        long endTime = System.nanoTime();
        long durationInMillis = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.println("Processed " + recordCount + " objects in " + durationInMillis + " ms");

        // Example assertion: processing should not take more than a certain amount of time
        assertTrue(durationInMillis < 200, "Performance test failed: took longer than expected");
    }
}
