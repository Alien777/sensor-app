package pl.lasota.sensor.flows;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import pl.lasota.sensor.core.models.device.Device;
import pl.lasota.sensor.core.models.mqtt.payload.MessageType;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.models.sensor.SingleAdcSignal;
import pl.lasota.sensor.core.restapi.SensorApiEndpoint;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.DeviceUtilsService;
import pl.lasota.sensor.flows.nodes.builder.FlowsBuilder;
import pl.lasota.sensor.flows.nodes.builder.NodeCreatorFactory;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.nodes.start.ListeningSensorNode;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;

import java.time.Duration;
import java.util.HashMap;

class ManagerFlowsTest {
    private TaskScheduler ts;
    private SensorListeningManager slm;
    @Mock
    private SensorApiEndpoint saeMock;
    @Mock
    private DeviceService dsMock;
    @Mock
    private DeviceUtilsService dusMock;
    @Mock
    private SingleAdcSignal sensorMock;
    @Mock
    private Device deviceMock;

    private NodeCreatorFactory.Factory factory;
    private GlobalContext globalContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ts = new SimpleAsyncTaskScheduler();
        slm = new SensorListeningManager();
        NodeCreatorFactory ncf = new NodeCreatorFactory(ts, slm, saeMock, dsMock, dusMock);
        globalContext = new GlobalContext();
        factory = ncf.create(globalContext);
    }

    @Test
    void listening_sensor_execute_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceId"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.SINGLE_ADC_SIGNAL);
        Mockito.when(deviceMock.getId()).thenReturn("deviceId");

        Node root = factory.listeningSensorNode("id", "memberKey", ListeningSensorNode.Data.create("deviceId", MessageType.SINGLE_ADC_SIGNAL));
        NodeMock node = Mockito.mock(NodeMock.class);
        FlowsBuilder.root(root)
                .add(root, node);


        slm.broadcast(sensorMock);
        slm.broadcast(sensorMock);

        Mockito.verify(node, Mockito.times(2)).execute(Mockito.any());
    }

    @Test
    void cron_node_execute_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceId"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.SINGLE_ADC_SIGNAL);
        Mockito.when(deviceMock.getId()).thenReturn("deviceId");

        Node root = factory.cronNode("id", "0/1 * * ? * *");
        NodeMock node = Mockito.mock(NodeMock.class);
        FlowsBuilder.root(root)
                .add(root, node);

        Thread.sleep(2000);

        Mockito.verify(node, Mockito.times(2)).execute(Mockito.any());
    }


    @Test
    void cron_node_execute_new_thread_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceId"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.SINGLE_ADC_SIGNAL);
        Mockito.when(deviceMock.getId()).thenReturn("deviceId");

        Node root = factory.listeningSensorNode("id", "memberKey", ListeningSensorNode.Data.create("deviceId", MessageType.SINGLE_ADC_SIGNAL));
        Node async = factory.asyncNodeCreator("id1");
        Node sleep = factory.sleepNode("id3", 1);

        NodeMock nodeMainThreadEnd = Mockito.mock(NodeMock.class);
        NodeMock nodeAsyncEnd = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root)
                .add(root, nodeMainThreadEnd)
                .add(root, async)
                .add(async, sleep)
                .add(sleep, nodeAsyncEnd);


        slm.broadcast(sensorMock);
        Thread.sleep(Duration.ofSeconds(2));

        InOrder inOrder = Mockito.inOrder(nodeMainThreadEnd, nodeAsyncEnd);
        inOrder.verify(nodeMainThreadEnd, Mockito.times(1)).execute(Mockito.any());
        inOrder.verify(nodeAsyncEnd, Mockito.times(1)).execute(Mockito.any());

    }


    @Test
    void js_condition_true_variable_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceId"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.SINGLE_ADC_SIGNAL);
        Mockito.when(deviceMock.getId()).thenReturn("deviceId");

        Node root = factory.listeningSensorNode("id", "memberKey", ListeningSensorNode.Data.create("deviceId", MessageType.SINGLE_ADC_SIGNAL));
        Node executeIfNode = factory.executeCodeNode("id1", "result=true");
        NodeMock nodeEnd = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root)
                .add(root, executeIfNode)
                .add(executeIfNode, nodeEnd);


        slm.broadcast(sensorMock);

        Mockito.verify(nodeEnd, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void js_condition_false_variable_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceId"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.SINGLE_ADC_SIGNAL);
        Mockito.when(deviceMock.getId()).thenReturn("deviceId");


        Node root = factory.listeningSensorNode("id", "memberKey", ListeningSensorNode.Data.create("deviceId", MessageType.DEVICE_CONNECTED));
        globalContext.getVariables().put("my_var", false);
        Node executeIfNode = factory.executeCodeNode("id1", "result = g_c.my_var");
        NodeMock nodeEnd = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root)
                .add(root, executeIfNode)
                .add(executeIfNode, nodeEnd);


        slm.broadcast(sensorMock);

        Mockito.verify(nodeEnd, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void js_override_java_variable() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceId"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.SINGLE_ADC_SIGNAL);
        Mockito.when(deviceMock.getId()).thenReturn("deviceId");

        globalContext.getVariables().put("my_var", true);

        Node root = factory.listeningSensorNode("id1", "memberKey", ListeningSensorNode.Data.create("deviceId", MessageType.SINGLE_ADC_SIGNAL));

        Node executeCodeNode = factory.executeCodeNode("id2", """
                g_c.my_var=12
                g_c.my_var_new=18
                l_c.new_value="test"
                let my_var_1=32
                result=true
                 """);
        NodeMock nodeEnd = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root)
                .add(root, executeCodeNode)
                .add(executeCodeNode, nodeEnd);

        Assertions.assertEquals(true, globalContext.getVariable("my_var"));

        slm.broadcast(sensorMock);

        Mockito.verify(nodeEnd, Mockito.times(1)).execute(Mockito.any());
        Assertions.assertEquals(12, globalContext.getVariable("my_var"));

        Mockito.verify(nodeEnd, Mockito.times(1)).execute(Mockito.any());
        Assertions.assertEquals(18, globalContext.getVariable("my_var_new"));
    }


}