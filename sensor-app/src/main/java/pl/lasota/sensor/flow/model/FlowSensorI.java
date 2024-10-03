package pl.lasota.sensor.flow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;
import pl.lasota.sensor.payload.PayloadType;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class FlowSensorI implements Serializable {
    private PayloadType payloadType;
    private String memberId;
    private String deviceId;
    private UUID requestId;
    private PayloadParser<?, String> payload;
}
