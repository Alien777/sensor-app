package pl.lasota.sensor.flows.nodes.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.entities.Member;
import pl.lasota.sensor.core.entities.device.Device;
import pl.lasota.sensor.core.exceptions.*;
import pl.lasota.sensor.core.entities.device.DeviceConfig;
import pl.lasota.sensor.core.entities.mqtt.payload.to.ConfigPayload;
import pl.lasota.sensor.core.apis.SensorMicroserviceEndpoint;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.DeviceUtilsService;
import pl.lasota.sensor.core.service.MemberService;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.nodes.*;
import pl.lasota.sensor.flows.nodes.nodes.ListeningSensorNode;
import pl.lasota.sensor.flows.nodes.nodes.AsyncNode;
import pl.lasota.sensor.flows.nodes.nodes.CronNode;
import pl.lasota.sensor.flows.nodes.nodes.SleepNode;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NodeCreatorFactory {

    private final TaskScheduler taskScheduler;
    private final SensorListeningManager slm;
    private final SensorMicroserviceEndpoint sae;
    private final DeviceService ds;
    private final MemberService ms;
    private final DeviceUtilsService dus;

    public Factory create() throws NotFoundMemberException {
        return new Factory(new GlobalContext(ms.loggedUser()));
    }

    public Factory create(GlobalContext globalContext) {
        return new Factory(globalContext);
    }

    public class Factory {
        private final GlobalContext globalContext;

        private Factory(GlobalContext globalContext) {
            this.globalContext = globalContext;
        }

        public final Node asyncNodeCreator(String ref) {
            return new AsyncNode(ref, globalContext);
        }

        public final Node executeCodeNode(String ref, String code) {
            return new ExecuteCodeNode(ref, globalContext, code);
        }


        public final Node sleepNode(String ref, long second) {
            return new SleepNode(ref, globalContext, second);
        }


        public final Node cronNode(String ref, String cron) {
            return new CronNode(ref, globalContext, taskScheduler, cron);
        }

        public Node requestAnalogData(String ref, RequestAnalogDataNode.Data data) throws NotFoundMemberException, NotFoundDeviceException {
            Member member = ms.loggedMember();
            Optional<Device> optionalDevice = ds.getDevice(member.getId(), data.getDeviceId());
            if (optionalDevice.isEmpty()) {
                throw new NotFoundDeviceException();
            }
            return new RequestAnalogDataNode(ref, globalContext, data, sae);
        }

        public final Node listeningSensorNode(String ref, ListeningSensorNode.Data data) throws NotFoundDeviceException, FlowException, NotFoundMemberException {
            Member member = ms.loggedMember();
            Optional<Device> optionalDevice = ds.getDevice(member.getId(), data.getDeviceId());
            if (optionalDevice.isEmpty()) {
                throw new NotFoundDeviceException();
            }
            return new ListeningSensorNode(ref, globalContext, data, slm);
        }

        public final Node sendPwmValueNode(String ref, SendPwmValueNode.Data data) throws NotFoundDeviceException, JsonProcessingException, NotFoundPinException, NotFoundMemberException {
            Member member = ms.loggedMember();
            Optional<Device> deviceOptional = ds.getDevice(member.getId(), data.getDeviceId());
            if (deviceOptional.isEmpty()) {
                throw new NotFoundDeviceException();
            }

            DeviceConfig deviceConfig = ds.currentDeviceConfig(member.getId(), data.getDeviceId());
            ConfigPayload configPayload = dus.mapConfigToObject(deviceConfig.getConfig());
            boolean b = configPayload.getPwmConfig().stream().anyMatch(pwmConfig -> pwmConfig.getPin() == data.getPin());
            if (!b) {
                throw new NotFoundPinException();
            }

            return new SendPwmValueNode(ref, globalContext, data, sae);
        }


    }


}
