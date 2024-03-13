package pl.lasota.sensor.flows.nodes.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.exceptions.FlowException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceConfigException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceException;
import pl.lasota.sensor.core.exceptions.NotFoundPinException;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.models.mqtt.payload.MessageType;
import pl.lasota.sensor.core.models.mqtt.payload.to.ConfigPayload;
import pl.lasota.sensor.core.restapi.SensorApiEndpoint;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.DeviceUtilsService;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.nodes.*;
import pl.lasota.sensor.flows.nodes.nodes.start.ListeningSensorNode;
import pl.lasota.sensor.flows.nodes.nodes.AsyncNode;
import pl.lasota.sensor.flows.nodes.nodes.start.CronNode;
import pl.lasota.sensor.flows.nodes.nodes.SleepNode;
import pl.lasota.sensor.flows.nodes.nodes.WaitNode;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NodeCreatorFactory {

    private final TaskScheduler taskScheduler;
    private final SensorListeningManager slm;
    private final SensorApiEndpoint sae;
    private final DeviceService ds;
    private final DeviceUtilsService dus;

    public Factory create() {
        return new Factory(new GlobalContext());
    }

    public Factory create(GlobalContext globalContext) {
        return new Factory(globalContext);
    }

    public class Factory {
        private final GlobalContext globalContext;

        private Factory(GlobalContext globalContext) {
            this.globalContext = globalContext;
        }

        public final Node asyncNodeCreator(String id) {
            return new AsyncNode(id, globalContext);
        }

        public final Node waitNode(String id, String waitForThread, long second) {
            return new WaitNode(id, globalContext, waitForThread, second);
        }

        public final Node executeCodeNode(String id, String code) {
            return new ExecuteCodeNode(id, globalContext, code);
        }


        public final Node sleepNode(String id, long second) {
            return new SleepNode(id, globalContext, second);
        }


        public final Node cronNode(String id, String cron) {
            return new CronNode(id, globalContext, taskScheduler, cron);
        }


        public final Node listeningSensorNode(String id, String memberKey, ListeningSensorNode.Data data) throws NotFoundDeviceException, FlowException {
            boolean deviceExist = ds.isDeviceExist(memberKey, data.getDeviceId());
            if (deviceExist) {
                return new ListeningSensorNode(id, globalContext, data, slm);
            }
            throw new NotFoundDeviceException();
        }

        public final Node sendPwmValueNode(String id, SendPwmValueNode.Data data) throws NotFoundDeviceException, NotFoundDeviceConfigException, JsonProcessingException, NotFoundPinException {
            boolean deviceExist = ds.isDeviceExist(data.getMemberKey(), data.getDeviceId());
            if (!deviceExist) {
                throw new NotFoundDeviceException();
            }
            DeviceConfig deviceConfig = ds.currentDeviceConfig(data.getMemberKey(), data.getDeviceId());
            ConfigPayload configPayload = dus.mapConfigToObject(deviceConfig.getConfig());
            boolean b = configPayload.getPwmConfig().stream().anyMatch(pwmConfig -> pwmConfig.getPin() == data.getPin());
            if (!b) {
                throw new NotFoundPinException();
            }

            return new SendPwmValueNode(id, globalContext, data, sae);

        }
    }


}
