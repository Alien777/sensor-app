package pl.lasota.sensor.core.apis.model.flow;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageType;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FlowSensorAnalogT.class, name = "ANALOG")
})
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
