package pl.lasota.sensor.flows.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.flows.exceptions.SensorFlowException;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;
import pl.lasota.sensor.flows.nodes.utils.NodeUtils;
import pl.lasota.sensor.internal.apis.api.SensorMicroserviceEndpoint;
import pl.lasota.sensor.internal.apis.api.device.SendPwmI;

import java.util.List;
import java.util.Optional;

import static pl.lasota.sensor.flows.nodes.builder.ParserFlows.fInteger;
import static pl.lasota.sensor.flows.nodes.builder.ParserFlows.fString;

@FlowNode
public class SendPwmValueNode extends Node {

    private final Data data;
    private final SensorMicroserviceEndpoint sensorMicroserviceEndpoint;

    private SendPwmValueNode(String id, GlobalContext globalContext, Data data, SensorMicroserviceEndpoint sensorMicroserviceEndpoint) {
        super(id, globalContext);
        this.data = data;
        this.sensorMicroserviceEndpoint = sensorMicroserviceEndpoint;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        Integer pin = fInteger(node, "pin");
        String valueKey = fString(node, "valueVariable");
        Data data = Data.create(deviceId, valueKey, pin);
        SensorMicroserviceEndpoint sae = context.getBean(SensorMicroserviceEndpoint.class);
        List<Integer> configPwmPins = sae.getConfigPwmPins(data.getDeviceId());
        if (!configPwmPins.contains(data.getPin())) {
            throw new SensorFlowException("Pwm pin not found");
        }
        return new SendPwmValueNode(ref, globalContext, data, sae);
    }

    @Override
    public void execute(LocalContext localContext) {
        Optional<Long> value = NodeUtils.getValue(data.valueVariable, localContext, globalContext, Long.class);
        if (value.isPresent()) {
            try {
                sensorMicroserviceEndpoint.sendPwmValueToDevice(new SendPwmI(data.deviceId, data.pin, value.get()));
                super.execute(localContext);
            } catch (Exception e) {
                throw new SensorFlowException("Occurred problem with send pwm value to device", e);
            }
        }
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final String valueVariable;
        private final int pin;
    }
}
