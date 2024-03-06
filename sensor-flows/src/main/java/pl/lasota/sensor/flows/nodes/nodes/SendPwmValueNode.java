package pl.lasota.sensor.flows.nodes.nodes;

import pl.lasota.sensor.core.models.rest.SendPwmS;
import pl.lasota.sensor.core.restapi.SensorApiEndpoint;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

@FlowNode
public class SendPwmValueNode extends Node {

    private final int pin;
    private final String valueKey;
    private final String deviceId;
    private final String memberId;
    private final SensorApiEndpoint sensorApiEndpoint;

    public SendPwmValueNode(PrivateContext privateContext, String deviceId, String memberId, int pin, String valueKey, SensorApiEndpoint sensorApiEndpoint) {
        super(privateContext);
        this.deviceId = deviceId;
        this.memberId = memberId;
        this.pin = pin;
        this.valueKey = valueKey;
        this.sensorApiEndpoint = sensorApiEndpoint;
    }

    @Override
    public void execute() throws Exception {
        Long valueId = (Long) privateContext.getVariable(valueKey);
        sensorApiEndpoint.pwmValue(new SendPwmS(memberId, deviceId, pin, valueId));
        super.execute();
    }
}
