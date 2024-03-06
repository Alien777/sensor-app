package pl.lasota.sensor.flows.nodes.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceConfigException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceException;
import pl.lasota.sensor.core.exceptions.NotFoundPinException;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.models.mqtt.payload.to.ConfigPayload;
import pl.lasota.sensor.core.restapi.SensorApiEndpoint;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.DeviceUtilsService;
import pl.lasota.sensor.flows.nodes.nodes.*;
import pl.lasota.sensor.flows.nodes.nodes.threads.AsyncNode;
import pl.lasota.sensor.flows.nodes.nodes.threads.CronNode;
import pl.lasota.sensor.flows.nodes.nodes.threads.SleepNode;
import pl.lasota.sensor.flows.nodes.nodes.threads.WaitNode;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;
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
        return new Factory(new PrivateContext());
    }

    public Factory create(PrivateContext privateContext) {
        return new Factory(privateContext);
    }

    public class Factory {
        private PrivateContext privateContext;

        private Factory(PrivateContext privateContext) {
            this.privateContext = privateContext;
        }

        public final Node asyncNodeCreator() {
            return new AsyncNode(privateContext);
        }

        public Node asyncNodeCreator(PrivateContext privateContext) {
            this.privateContext = privateContext;
            return new AsyncNode(privateContext);
        }

        public final Node waitNode(String waitForThread, long second) {
            return new WaitNode(privateContext, waitForThread, second);
        }

        public final Node executeCodeNode(String code) {
            return new ExecuteCodeNode(privateContext, code);
        }

        public final Node variableNode(boolean fastInitialization, Map<String, Object> variables) {
            return new VariableNode(privateContext, fastInitialization, variables);
        }

        public final Node sleepNode(long second) {
            return new SleepNode(privateContext, second);
        }

        public final Node cronNode(String cron, boolean fastInitialization) {
            return cronNode(cron, fastInitialization, null);
        }

        public final Node cronNode(String cron, boolean fastInitialization, String timesExecuteVariableKey) {
            return new CronNode(privateContext, taskScheduler, fastInitialization, cron, timesExecuteVariableKey);
        }

        public final Node executeIfNode(String condition) {
            return new ExecuteIfNode(privateContext, condition);
        }

        public final Node listeningSensorNode(String memberKey, String deviceKey) throws NotFoundDeviceException {
            boolean deviceExist = ds.isDeviceExist(memberKey, deviceKey);
            if (deviceExist) {
                return new ListeningSensorNode(privateContext, deviceKey, slm);
            }
            throw new NotFoundDeviceException();
        }

        public final Node sendPwmValueNode(String memberKey, String deviceKey, int pin, String variableKey) throws NotFoundDeviceException, NotFoundDeviceConfigException, JsonProcessingException, NotFoundPinException {
            boolean deviceExist = ds.isDeviceExist(memberKey, deviceKey);
            if (!deviceExist) {
                throw new NotFoundDeviceException();
            }
            DeviceConfig deviceConfig = ds.currentDeviceConfig(memberKey, deviceKey);
            ConfigPayload configPayload = dus.mapConfigToObject(deviceConfig.getConfig());
            boolean b = configPayload.getPwmConfig().stream().anyMatch(pwmConfig -> pwmConfig.getPin() == pin);
            if (!b) {
                throw new NotFoundPinException();
            }

            return new SendPwmValueNode(privateContext, memberKey, deviceKey, pin, variableKey, sae);

        }
    }


}
