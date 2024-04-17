package pl.lasota.sensor.internal.apis.api.flows;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@JsonTypeName("ANALOG")
public class FlowSensorAnalogI extends FlowSensorI implements Serializable {
    private Integer pin;
    private Double value;

    public FlowSensorAnalogI(FlowSensorI flowSensorI) {
        setDeviceId(flowSensorI.getDeviceId());
        setMemberId(flowSensorI.getMemberId());
        setMessageType(flowSensorI.getMessageType());
    }

    public FlowSensorAnalogI setPin(Integer pin) {
        this.pin = pin;
        return this;
    }

    public FlowSensorAnalogI setValue(Double value) {
        this.value = value;
        return this;
    }
}
