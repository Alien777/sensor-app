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
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.restapi.SensorApiEndpoint;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.DeviceUtilsService;
import pl.lasota.sensor.flows.nodes.builder.FlowsBuilder;
import pl.lasota.sensor.flows.nodes.builder.NodeCreatorFactory;
import pl.lasota.sensor.flows.nodes.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;
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
    private Sensor sensorMock;
    @Mock
    private Device deviceMock;

    private NodeCreatorFactory.Factory factory;
    private PrivateContext privateContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ts = new SimpleAsyncTaskScheduler();
        slm = new SensorListeningManager();
        NodeCreatorFactory ncf = new NodeCreatorFactory(ts, slm, saeMock, dsMock, dusMock);
        privateContext = new PrivateContext();
        factory = ncf.create(privateContext);
    }

    @Test
    void listening_sensor_execute_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceKey"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(deviceMock.getDeviceKey()).thenReturn("deviceKey");

        Node root = factory.listeningSensorNode("memberKey", "deviceKey");
        NodeMock node = Mockito.mock(NodeMock.class);
        FlowsBuilder.root(root)
                .add(root, node);


        slm.broadcast(sensorMock);
        slm.broadcast(sensorMock);

        Mockito.verify(node, Mockito.times(2)).execute();
    }

    @Test
    void cron_node_execute_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceKey"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(deviceMock.getDeviceKey()).thenReturn("deviceKey");

        Node root = factory.cronNode("0/1 * * ? * *");
        NodeMock node = Mockito.mock(NodeMock.class);
        FlowsBuilder.root(root)
                .add(root, node);

        Thread.sleep(2000);

        Mockito.verify(node, Mockito.times(2)).execute();
    }


    @Test
    void cron_node_execute_new_thread_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceKey"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(deviceMock.getDeviceKey()).thenReturn("deviceKey");

        Node root = factory.listeningSensorNode("memberKey", "deviceKey");
        Node async = factory.asyncNodeCreator();
        Node sleep = factory.sleepNode(1);

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
        inOrder.verify(nodeMainThreadEnd, Mockito.times(1)).execute();
        inOrder.verify(nodeAsyncEnd, Mockito.times(1)).execute();

    }


    @Test
    void js_condition_true_variable_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceKey"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(deviceMock.getDeviceKey()).thenReturn("deviceKey");
        Mockito.when(deviceMock.getDeviceKey()).thenReturn("deviceKey");

        HashMap<String, Object> variables = new HashMap<>();
        variables.put("my_var", true);

        Node root = factory.listeningSensorNode("memberKey", "deviceKey");
        Node variableNode = factory.variableNode(true, variables);
        Node executeIfNode = factory.executeIfNode("my_var");
        NodeMock nodeEnd = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root)
                .add(root, variableNode)
                .add(variableNode, executeIfNode)
                .add(executeIfNode, nodeEnd);


        slm.broadcast(sensorMock);

        Mockito.verify(nodeEnd, Mockito.times(1)).execute();
    }

    @Test
    void js_condition_false_variable_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceKey"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(deviceMock.getDeviceKey()).thenReturn("deviceKey");

        HashMap<String, Object> variables = new HashMap<>();
        variables.put("my_var", false);

        Node root = factory.listeningSensorNode("memberKey", "deviceKey");
        Node variableNode = factory.variableNode(true, variables);
        Node executeIfNode = factory.executeIfNode("my_var");
        NodeMock nodeEnd = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root)
                .add(root, variableNode)
                .add(variableNode, executeIfNode)
                .add(executeIfNode, nodeEnd);


        slm.broadcast(sensorMock);

        Mockito.verify(nodeEnd, Mockito.times(0)).execute();
    }

    @Test
    void js_override_java_variable() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberKey"), Mockito.same("deviceKey"))).thenReturn(true);
        Mockito.when(sensorMock.getDevice()).thenReturn(deviceMock);
        Mockito.when(deviceMock.getDeviceKey()).thenReturn("deviceKey");

        HashMap<String, Object> variables = new HashMap<>();
        variables.put("my_var", 10);

        Node root = factory.listeningSensorNode("memberKey", "deviceKey");
        Node variableNode = factory.variableNode(true, variables);
        Node executeIfNode = factory.executeCodeNode("""
                my_var=12
                let my_var_1=32
                 """);
        NodeMock nodeEnd = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root)
                .add(root, variableNode)
                .add(variableNode, executeIfNode)
                .add(executeIfNode, nodeEnd);

        Assertions.assertEquals(10, privateContext.getVariable("my_var"));
        slm.broadcast(sensorMock);

        Mockito.verify(nodeEnd, Mockito.times(1)).execute();
        Assertions.assertEquals(12, privateContext.getVariable("my_var"));
    }


}