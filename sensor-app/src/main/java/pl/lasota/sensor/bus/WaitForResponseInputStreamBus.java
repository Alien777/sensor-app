package pl.lasota.sensor.bus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.bus.broadcast.CustomerBroadcast;
import pl.lasota.sensor.bus.broadcast.impl.WaitForResponseBroadcasterStream;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WaitForResponseInputStreamBus extends CustomerBroadcast<String, UUID> {

    private final WaitForResponseBroadcasterStream broadcast;

    public WaitForResponseInputStreamBus() throws Exception {
        broadcast = new WaitForResponseBroadcasterStream(super.getCustomers());
    }

    @Override
    public WaitForResponseBroadcasterStream takeBroadcaster(String s) {
        return broadcast;
    }
}
