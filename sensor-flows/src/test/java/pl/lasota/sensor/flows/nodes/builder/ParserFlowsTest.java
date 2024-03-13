package pl.lasota.sensor.flows.nodes.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import pl.lasota.sensor.core.exceptions.FlowException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceConfigException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceException;
import pl.lasota.sensor.core.exceptions.NotFoundPinException;
import pl.lasota.sensor.core.models.device.Device;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.restapi.SensorApiEndpoint;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.DeviceUtilsService;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.nodes.*;
import pl.lasota.sensor.flows.nodes.nodes.start.ListeningSensorNode;
import pl.lasota.sensor.flows.nodes.nodes.AsyncNode;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;

import java.util.Map;

class ParserFlowsTest {


    private TaskScheduler ts;
    private SensorListeningManager slm;
    @Mock
    private SensorApiEndpoint saeMock;
    @Mock
    private DeviceService dsMock;
    @Mock
    private DeviceUtilsService dusMock;
    @Mock
    private Sensor sensorMock;
    @Mock
    private Device deviceMock;

    private NodeCreatorFactory ncf;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ts = new SimpleAsyncTaskScheduler();
        slm = new SensorListeningManager();
        ncf = new NodeCreatorFactory(ts, slm, saeMock, dsMock, dusMock);
    }

    @Test
    public void root_flow_test() throws JsonProcessingException, NotFoundPinException, NotFoundDeviceConfigException, NotFoundDeviceException, FlowException {
        Mockito.when(dsMock.isDeviceExist(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Node root = new ParserFlows().flows("""
                [
                {
                    "_ref": "123df",
                    "_root": true,
                    "_name": "ListeningSensorNode",
                    "deviceId": "device",
                    "memberKey": "member",
                    "type": "SINGLE_ADC_SIGNAL",
                    "pin": "1",
                    "valueVariableName": "value",
                    "_childed":[]         
                }           
                ]
                """, ncf.create());

        Assertions.assertInstanceOf(ListeningSensorNode.class, root);
    }


    @Test
    public void flow_1_test() throws JsonProcessingException, NotFoundPinException, NotFoundDeviceConfigException, NotFoundDeviceException, FlowException {
        Mockito.when(dsMock.isDeviceExist(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);

        Node root = new ParserFlows().flows("""
                [
                {
                    "_ref": "root",
                    "_root": true,
                    "_name": "ListeningSensorNode",
                    "deviceId": "device",
                    "memberKey": "member",
                     "type": "SINGLE_ADC_SIGNAL",
                    "pin": "1",
                    "valueVariableName": "value",
                    "_childed":["code_ref_1","if_ref_1"]
                },
                 {
                    "_ref": "code_ref_1",
                    "_name": "ExecuteCodeNode",
                    "code": "let a=0",
                    "_childed":["if_ref_1","async_ref_1"]
                },
                   {
                    "_ref": "if_ref_1",
                    "_name": "ExecuteCodeNode",
                    "code": "0=0",
                    "_childed":[]         
                },
                   {
                    "_ref": "async_ref_1",
                    "_name": "AsyncNode",
                    "_childed":[]         
                }            
                ]
                """, ncf.create());

        Assertions.assertInstanceOf(ListeningSensorNode.class, root);
        Assertions.assertInstanceOf(ExecuteCodeNode.class, root.getNodes().get(0));
        Assertions.assertInstanceOf(ExecuteCodeNode.class, root.getNodes().get(1));

        Node code_ref_1 = root.getNodes().get(0);
        Assertions.assertInstanceOf(ExecuteCodeNode.class, code_ref_1);
        Assertions.assertInstanceOf(AsyncNode.class, code_ref_1.getNodes().get(0));
        Assertions.assertInstanceOf(ExecuteCodeNode.class, code_ref_1.getNodes().get(1));
    }

}