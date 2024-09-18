package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.device.model.DigitalSetUpMessage;
import pl.lasota.sensor.flow.services.keeper.KeeperForSetUp;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fInteger;
import static pl.lasota.sensor.flow.services.nodes.builder.ParserFlows.fString;

@FlowNode
public class DigitalSetUpNode extends Node {

    private final Data data;
    private final DeviceSendMessageInterface deviceApiInterface;
    private final KeeperForSetUp keeperForSetUp;

    private DigitalSetUpNode(String id, GlobalContext globalContext, Data data, DeviceSendMessageInterface deviceApiInterface, KeeperForSetUp keeperForSetUp) {
        super(id, globalContext);
        this.data = data;
        this.deviceApiInterface = deviceApiInterface;
        this.keeperForSetUp = keeperForSetUp;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String deviceId = fString(node, "deviceId");
        int gpio = fInteger(node, "gpio");
        Integer mode = fInteger(node, "mode");

        KeeperForSetUp kfsu = context.getBean(KeeperForSetUp.class);
        kfsu.addDigital(deviceId, gpio);

        Data data = Data.create(deviceId, gpio, mode);
        DeviceSendMessageInterface dsmi = context.getBean(DeviceSendMessageInterface.class);
        return new DigitalSetUpNode(ref, globalContext, data, dsmi, kfsu);
    }

    @Override
    protected void fireChildNodes(LocalContext localContext) throws Exception {
        deviceApiInterface.sendDigitalSetUp(new DigitalSetUpMessage(data.deviceId, data.gpio, data.mode));
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
        private final int mode;
    }
}
