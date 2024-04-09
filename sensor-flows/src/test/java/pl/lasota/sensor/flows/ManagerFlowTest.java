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
import pl.lasota.sensor.core.apis.SensorMicroserviceEndpoint;
import pl.lasota.sensor.core.apis.model.flow.FlowSensorAnalogT;
import pl.lasota.sensor.core.common.User;
import pl.lasota.sensor.core.entities.DeviceToken;
import pl.lasota.sensor.core.entities.Member;
import pl.lasota.sensor.core.entities.Role;
import pl.lasota.sensor.core.entities.device.Device;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageType;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.DeviceUtilsService;
import pl.lasota.sensor.core.service.MemberService;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.StartFlowNode;
import pl.lasota.sensor.flows.nodes.builder.FlowsBuilder;
import pl.lasota.sensor.flows.nodes.builder.NodeCreatorFactory;
import pl.lasota.sensor.flows.nodes.nodes.ListeningSensorNode;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

class ManagerFlowTest {
    private TaskScheduler ts;
    private SensorListeningManager slm;
    @Mock
    private SensorMicroserviceEndpoint saeMock;
    @Mock
    private DeviceService dsMock;

    @Mock
    private MemberService msMock;

    @Mock
    private DeviceUtilsService dusMock;

    @Mock
    private Device deviceMock;

    @Mock
    private Member memberMock;

    @Mock
    private DeviceToken deviceTokenMock;

    @Mock
    private FlowSensorAnalogT sensorMock;

    private NodeCreatorFactory.Factory factory;
    private GlobalContext globalContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ts = new SimpleAsyncTaskScheduler();
        slm = new SensorListeningManager();
        NodeCreatorFactory ncf = new NodeCreatorFactory(ts, slm, saeMock, dsMock, msMock, dusMock);
        globalContext = new GlobalContext(User.builder().id("memberId").roles(Collections.singleton(Role.ROLE_USER)).build());
        factory = ncf.create(globalContext);
    }

    @Test
    void listening_sensor_execute_test() throws Exception {

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);

        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(deviceMock.getCurrentDeviceToken()).thenReturn(deviceTokenMock);
        Mockito.when(deviceTokenMock.getToken()).thenReturn("token");
        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(dsMock.getDevice(Mockito.same("memberId"), Mockito.same("deviceId")))
                .thenReturn(Optional.of(deviceMock));

        Mockito.when(sensorMock.getDeviceId()).thenReturn("deviceId");
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.ANALOG);

        Node root = factory.listeningSensorNode("id", ListeningSensorNode.Data.create("deviceId", null, null, MessageType.ANALOG));
        NodeMock node = Mockito.mock(NodeMock.class);
        FlowsBuilder.root(root)
                .add(root, node);

        ((StartFlowNode) root).start();
        slm.broadcast(sensorMock);
        slm.broadcast(sensorMock);

        Mockito.verify(node, Mockito.times(2)).execute(Mockito.any());
    }

    @Test
    void cron_node_execute_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberId"), Mockito.same("deviceId"))).thenReturn(true);

        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(deviceMock.getCurrentDeviceToken()).thenReturn(deviceTokenMock);
        Mockito.when(deviceTokenMock.getToken()).thenReturn("token");
        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(dsMock.getDevice(Mockito.same("memberId"), Mockito.same("deviceId")))
                .thenReturn(Optional.of(deviceMock));


        Mockito.when(sensorMock.getDeviceId()).thenReturn("deviceId");
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.ANALOG);

        Node root = factory.cronNode("id", "0/1 * * ? * *");
        NodeMock node = Mockito.mock(NodeMock.class);
        FlowsBuilder.root(root)
                .add(root, node);
        ((StartFlowNode) root).start();
        Thread.sleep(2000);

        Mockito.verify(node, Mockito.times(2)).execute(Mockito.any());
    }

    @Test
    void cron_node_execute_stop_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberId"), Mockito.same("deviceId"))).thenReturn(true);

        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(deviceMock.getCurrentDeviceToken()).thenReturn(deviceTokenMock);
        Mockito.when(deviceTokenMock.getToken()).thenReturn("token");
        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(dsMock.getDevice(Mockito.same("memberId"), Mockito.same("deviceId")))
                .thenReturn(Optional.of(deviceMock));


        Mockito.when(sensorMock.getDeviceId()).thenReturn("deviceId");
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.ANALOG);

        Node root = factory.cronNode("id", "0/1 * * ? * *");
        NodeMock node = Mockito.mock(NodeMock.class);
        FlowsBuilder.root(root)
                .add(root, node);
        ((StartFlowNode) root).start();
        Thread.sleep(3000);
        root.clear();
        Thread.sleep(3000);
        Mockito.verify(node, Mockito.times(3)).execute(Mockito.any());
    }


    @Test
    void cron_node_execute_new_thread_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberId"), Mockito.same("deviceId"))).thenReturn(true);

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(deviceMock.getCurrentDeviceToken()).thenReturn(deviceTokenMock);
        Mockito.when(deviceTokenMock.getToken()).thenReturn("token");
        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(dsMock.getDevice(Mockito.same("memberId"), Mockito.same("deviceId")))
               .thenReturn(Optional.of(deviceMock));


        Mockito.when(sensorMock.getDeviceId()).thenReturn("deviceId");
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.ANALOG);

        Node root = factory.listeningSensorNode("id", ListeningSensorNode.Data.create("deviceId", null, null, MessageType.ANALOG));
        Node async = factory.asyncNodeCreator("id1");
        Node sleep = factory.sleepNode("id3", 1);

        NodeMock nodeMainThreadEnd = Mockito.mock(NodeMock.class);
        NodeMock nodeAsyncEnd = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root)
                .add(root, nodeMainThreadEnd)
                .add(root, async)
                .add(async, sleep)
                .add(sleep, nodeAsyncEnd);

        ((StartFlowNode) root).start();
        slm.broadcast(sensorMock);
        Thread.sleep(Duration.ofSeconds(2));

        InOrder inOrder = Mockito.inOrder(nodeMainThreadEnd, nodeAsyncEnd);
        inOrder.verify(nodeMainThreadEnd, Mockito.times(1)).execute(Mockito.any());
        inOrder.verify(nodeAsyncEnd, Mockito.times(1)).execute(Mockito.any());

    }


    @Test
    void js_condition_true_variable_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberId"), Mockito.same("deviceId"))).thenReturn(true);

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(deviceMock.getCurrentDeviceToken()).thenReturn(deviceTokenMock);
        Mockito.when(deviceTokenMock.getToken()).thenReturn("token");
        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(dsMock.getDevice(Mockito.same("memberId"), Mockito.same("deviceId")))
                .thenReturn(Optional.of(deviceMock));


        Mockito.when(sensorMock.getDeviceId()).thenReturn("deviceId");
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.ANALOG);

        Node root = factory.listeningSensorNode("id", ListeningSensorNode.Data.create("deviceId", null, null, MessageType.ANALOG));
        Node executeIfNode = factory.executeCodeNode("id1", "result=true");
        NodeMock nodeEnd = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root)
                .add(root, executeIfNode)
                .add(executeIfNode, nodeEnd);

        ((StartFlowNode) root).start();
        slm.broadcast(sensorMock);

        Mockito.verify(nodeEnd, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void js_condition_false_variable_test() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberId"), Mockito.same("deviceId"))).thenReturn(true);

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(deviceMock.getCurrentDeviceToken()).thenReturn(deviceTokenMock);
        Mockito.when(deviceTokenMock.getToken()).thenReturn("token");
        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(dsMock.getDevice(Mockito.same("memberId"), Mockito.same("deviceId")))
                .thenReturn(Optional.of(deviceMock));

        Mockito.when(sensorMock.getDeviceId()).thenReturn("deviceId");
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.ANALOG);


        Node root = factory.listeningSensorNode("id", ListeningSensorNode.Data.create("deviceId", null, null, MessageType.DEVICE_CONNECTED));
        globalContext.getVariables().put("my_var", false);
        Node executeIfNode = factory.executeCodeNode("id1", "result = g_c.my_var");
        NodeMock nodeEnd = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root)
                .add(root, executeIfNode)
                .add(executeIfNode, nodeEnd);

        ((StartFlowNode) root).start();
        slm.broadcast(sensorMock);

        Mockito.verify(nodeEnd, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void js_override_java_variable() throws Exception {

        Mockito.when(dsMock.isDeviceExist(Mockito.same("memberId"), Mockito.same("deviceId"))).thenReturn(true);
        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(deviceMock.getCurrentDeviceToken()).thenReturn(deviceTokenMock);
        Mockito.when(deviceTokenMock.getToken()).thenReturn("token");
        Mockito.when(memberMock.getId()).thenReturn("memberId");
        Mockito.when(dsMock.getDevice(Mockito.same("memberId"), Mockito.same("deviceId")))
                .thenReturn(Optional.of(deviceMock));


        Mockito.when(sensorMock.getDeviceId()).thenReturn("deviceId");
        Mockito.when(sensorMock.getMessageType()).thenReturn(MessageType.ANALOG);

        globalContext.getVariables().put("my_var", true);

        Node root = factory.listeningSensorNode("id1", ListeningSensorNode.Data.create("deviceId", null, null, MessageType.ANALOG));

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
        ((StartFlowNode) root).start();
        slm.broadcast(sensorMock);

        Mockito.verify(nodeEnd, Mockito.times(1)).execute(Mockito.any());
        Assertions.assertEquals(12, globalContext.getVariable("my_var"));

        Mockito.verify(nodeEnd, Mockito.times(1)).execute(Mockito.any());
        Assertions.assertEquals(18, globalContext.getVariable("my_var_new"));
    }

}