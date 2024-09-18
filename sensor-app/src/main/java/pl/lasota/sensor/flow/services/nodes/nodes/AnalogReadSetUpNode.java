package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.device.model.AnalogReadSetUpMessage;
import pl.lasota.sensor.flow.services.keeper.KeeperForSetUp;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fInteger;
import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@FlowNode
public class AnalogReadSetUpNode extends Node {

    private final Data data;
    private final DeviceSendMessageInterface deviceApiInterface;
    private final KeeperForSetUp keeperForSetUp;

    private AnalogReadSetUpNode(String id, GlobalContext globalContext, Data data, DeviceSendMessageInterface deviceApiInterface, KeeperForSetUp keeperForSetUp) {
        super(id, globalContext);
        this.data = data;
        this.deviceApiInterface = deviceApiInterface;
        this.keeperForSetUp = keeperForSetUp;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        int gpio = fInteger(node, "gpio");
        Integer resolution = fInteger(node, "resolution");
        Data data = Data.create(deviceId, gpio, resolution);

        KeeperForSetUp kfsu = context.getBean(KeeperForSetUp.class);
        kfsu.addAnalog(deviceId, gpio);

        DeviceSendMessageInterface dsmi = context.getBean(DeviceSendMessageInterface.class);
        return new AnalogReadSetUpNode(ref, globalContext, data, dsmi, kfsu);
    }

    @Override
    protected void fireChildNodes(LocalContext localContext) throws Exception {
        deviceApiInterface.sendAnalogReadSetUp(new AnalogReadSetUpMessage(data.deviceId, data.gpio, data.resolution));
        super.fireChildNodes(localContext);
    }

    @Override
    public void clear() {
        keeperForSetUp.removeConfig(data.deviceId, data.gpio);
        super.clear();
    }

    @AllArgsConstructor(staticName = "create")
    @Getter
    public static class Data {
        private final String deviceId;
        private final int gpio;
        private final int resolution;
    }
}
