package pl.lasota.sensor.core.apis.model.flow;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageType;

@Data
@NoArgsConstructor
public class FlowSensorT {
    private MessageType messageType;
    private String memberId;
    private String deviceId;

    public FlowSensorT setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public FlowSensorT setMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public FlowSensorT setMessageType(MessageType messageType) {
        this.messageType = messageType;
        return this;
    }
}
