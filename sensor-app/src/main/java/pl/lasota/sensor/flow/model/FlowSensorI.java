package pl.lasota.sensor.flow.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lasota.sensor.payload.PayloadType;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "payloadType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FlowSensorAnalogI.class, name = "ANALOG")
})
public class FlowSensorI implements Serializable {
    private PayloadType payloadType;
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

    public FlowSensorI setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
        return this;
    }
}
