package pl.lasota.sensor.flows.nodes.builder;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.flows.exceptions.SensorFlowException;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.nodes.*;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;
import pl.lasota.sensor.internal.apis.api.SensorMicroserviceEndpoint;
import pl.lasota.sensor.internal.apis.api.device.DeviceI;
import pl.lasota.sensor.member.entities.Member;
import pl.lasota.sensor.member.services.MemberService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeCreatorFactory {

    private final TaskScheduler taskScheduler;
    private final SensorListeningManager slm;
    private final SensorMicroserviceEndpoint sae;
    private final MemberService ms;


    public Factory create() {
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

        public Node requestAnalogData(String ref, RequestAnalogDataNode.Data data) {
            Member member = ms.loggedMember();
            DeviceI deviceI = sae.get(data.getDeviceId());
            return new RequestAnalogDataNode(ref, globalContext, data, sae);
        }

        public final Node listeningSensorNode(String ref, ListeningSensorNode.Data data) {
            Member member = ms.loggedMember();
            DeviceI deviceI = sae.get(data.getDeviceId());
            return new ListeningSensorNode(ref, globalContext, data, slm);
        }

        public final Node sendPwmValueNode(String ref, SendPwmValueNode.Data data) {
            Member member = ms.loggedMember();
            DeviceI deviceI = sae.get(data.getDeviceId());
            List<Integer> configPwmPins = sae.getConfigPwmPins(data.getDeviceId());
            if (!configPwmPins.contains(data.getPin())) {
                throw new SensorFlowException("Pwm pin not found");
            }
            return new SendPwmValueNode(ref, globalContext, data, sae);
        }
    }
}
