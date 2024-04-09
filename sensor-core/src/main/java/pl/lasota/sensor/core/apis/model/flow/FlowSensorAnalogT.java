package pl.lasota.sensor.core.apis.model.flow;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@JsonTypeName("ANALOG")
public class FlowSensorAnalogT extends FlowSensorT {
    private Integer pin;
    private Double value;

    public FlowSensorAnalogT(FlowSensorT flowSensorT) {
        setDeviceId(flowSensorT.getDeviceId());
        setMemberId(flowSensorT.getMemberId());
        setMessageType(flowSensorT.getMessageType());
    }

    public FlowSensorAnalogT setPin(Integer pin) {
        this.pin = pin;
        return this;
    }

    public FlowSensorAnalogT setValue(Double value) {
        this.value = value;
        return this;
    }
}
