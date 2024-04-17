package pl.lasota.sensor.internal.apis.api.flows;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FlowSensorAnalogI.class, name = "ANALOG")
})
public class FlowSensorI implements Serializable {
    private String messageType;
    private String memberId;
    private String deviceId;

    public FlowSensorI setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public FlowSensorI setMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public FlowSensorI setMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }
}
