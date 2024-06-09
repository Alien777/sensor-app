package pl.lasota.sensor.bus;

import org.junit.jupiter.api.BeforeEach;
import pl.lasota.sensor.flow.model.FlowSensorI;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@Disabled
//@SpringBootTest
class FlowSensorICustomerBroadcastTest {

    //    @Autowired
    private FlowSensorIInputStreamBus flowSensorIInputStreamBus;

    @BeforeEach
    void setUp() throws IOException {
        // Set up any required resources or configuration
    }

    //    @Test
    void testPerformance() throws IOException, InterruptedException {
        final int recordCount = 50000;
        CountDownLatch latch = new CountDownLatch(recordCount);


        flowSensorIInputStreamBus.addConsumer((s, flowSensorI) -> latch.countDown());

        long startTime = System.nanoTime();

        for (int i = 0; i < recordCount; i++) {
            FlowSensorI flowSensorI = new FlowSensorI();
            flowSensorIInputStreamBus.takeBroadcaster("streamInfo").write(flowSensorI);
        }

        latch.await();

        long endTime = System.nanoTime();
        long durationInMillis = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.println("Processed " + recordCount + " objects in " + durationInMillis + " ms");

        assertTrue(durationInMillis < 3000, "Performance test failed: took longer than expected");
    }
}
