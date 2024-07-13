package pl.lasota.sensor.flow.services.nodes.send;

import java.util.UUID;

public interface ProvideRequest {
    UUID send() throws Exception;
}

