package pl.lasota.sensor.flow;


import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import pl.lasota.sensor.bus.FlowSensorIInputStreamBus;
import pl.lasota.sensor.bus.broadcast.impl.FlowSensorIBroadcasterStream;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.entities.Role;
import pl.lasota.sensor.flow.model.FlowSensorAnalogI;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.NodeStart;
import pl.lasota.sensor.flow.services.nodes.builder.FlowsBuilder;
import pl.lasota.sensor.flow.services.nodes.nodes.*;
import pl.lasota.sensor.flow.services.nodes.utils.FlowContext;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.member.services.MemberLoginService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagerFlowTest {

    @Mock
    private ApplicationContext ac;

    @Mock
    private JsonNode jn;

    @Mock
    private MemberLoginService msMock;

    @Mock
    private Member memberMock;

    FlowSensorAnalogI flowSensorAnalogI;

    private GlobalContext globalContext;

    private FlowContext flowContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        globalContext = new GlobalContext();
        flowContext = new FlowContext(Member.builder().id("memberId").role(Role.ROLE_USER).build());
        flowSensorAnalogI = new FlowSensorAnalogI();
        flowSensorAnalogI.setDeviceId("deviceId");
        flowSensorAnalogI.setMemberId("memberId");
        flowSensorAnalogI.setMessageType("ANALOG");
    }

    @Test
    void listening_sensor_execute_test() throws Exception {

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);

        JsonNode deviceIdMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("deviceId"))).thenReturn(deviceIdMock);
        Mockito.when(deviceIdMock.asText()).thenReturn("deviceId");

        JsonNode messageTypeMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("messageType"))).thenReturn(messageTypeMock);
        Mockito.when(messageTypeMock.asText()).thenReturn("ANALOG");

        FlowSensorIInputStreamBus slm = new FlowSensorIInputStreamBus();
        Mockito.when(ac.getBean(FlowSensorIInputStreamBus.class)).thenReturn(slm);

        Node root = ListeningSensorNode.create("id", globalContext, jn, ac);

        NodeMock node = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root).add(root, node);

        CountDownLatch latch = new CountDownLatch(2);
        Mockito.doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(node).fireChildNodes(Mockito.any());
        ((NodeStart) root).config(flowContext);
        try (FlowSensorIBroadcasterStream fs = slm.takeBroadcaster(null);) {
            fs.write(flowSensorAnalogI);
            fs.write(flowSensorAnalogI);
            latch.await(500, TimeUnit.MILLISECONDS);
        }
        Mockito.verify(node, Mockito.times(2)).fireChildNodes(Mockito.any());
    }

    //
    @Test
    void cron_node_execute_stop_test() throws Exception {

        Mockito.when(memberMock.getId()).thenReturn("memberId");


        TaskScheduler taskScheduler = new SimpleAsyncTaskScheduler();
        Mockito.when(ac.getBean(TaskScheduler.class)).thenReturn(taskScheduler);

        JsonNode messageTypeMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("cron"))).thenReturn(messageTypeMock);
        Mockito.when(messageTypeMock.asText()).thenReturn("5");

        Node root = CronNode.create("id", globalContext, jn, ac);
        NodeMock node = Mockito.mock(NodeMock.class);
        FlowsBuilder.root(root)
                .add(root, node);
        ((NodeStart) root).config(flowContext);
        Thread.sleep(5);
        root.clear();
        Thread.sleep(20);
        Mockito.verify(node, Mockito.times(1)).fireChildNodes(Mockito.any());
    }

    //    //
    @Test
    void event_node_execute_test() throws Exception {
        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        Mockito.when(memberMock.getId()).thenReturn("memberId");


        JsonNode deviceIdMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("deviceId"))).thenReturn(deviceIdMock);
        Mockito.when(deviceIdMock.asText()).thenReturn("deviceId");

        JsonNode messageTypeMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("messageType"))).thenReturn(messageTypeMock);
        Mockito.when(messageTypeMock.asText()).thenReturn("ANALOG");

        JsonNode IntervalSleepMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("sleepTime"))).thenReturn(IntervalSleepMock);
        Mockito.when(IntervalSleepMock.asLong()).thenReturn(1L);

        FlowSensorIInputStreamBus slm = new FlowSensorIInputStreamBus();
        Mockito.when(ac.getBean(FlowSensorIInputStreamBus.class)).thenReturn(slm);

        CountDownLatch latch = new CountDownLatch(1);
        slm.addConsumer((s, flowSensorI) -> latch.countDown());
        Node root = ListeningSensorNode.create("id", globalContext, jn, ac);

        Node async = AsyncNode.create("id1", globalContext, jn, ac);
        Node sleep = SleepNode.create("id3", globalContext, jn, ac);

        NodeMock nodeMainThreadEnd = Mockito.mock(NodeMock.class);
        NodeMock nodeAsyncEnd = Mockito.mock(NodeMock.class);

        FlowsBuilder.root(root)
                .add(root, nodeMainThreadEnd)
                .add(root, async)
                .add(async, sleep)
                .add(sleep, nodeAsyncEnd);

        ((NodeStart) root).config(flowContext);
        slm.takeBroadcaster(null).write(flowSensorAnalogI);
        latch.await(10, TimeUnit.MILLISECONDS);
        Thread.sleep(10);

        InOrder inOrder = Mockito.inOrder(nodeMainThreadEnd, nodeAsyncEnd);
        inOrder.verify(nodeMainThreadEnd, Mockito.times(1)).fireChildNodes(Mockito.any());
        inOrder.verify(nodeAsyncEnd, Mockito.times(1)).fireChildNodes(Mockito.any());

    }

    //
//    //
    @Test
    void js_condition_true_variable_test() throws Exception {

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        Mockito.when(memberMock.getId()).thenReturn("memberId");

        FlowSensorIInputStreamBus slm = new FlowSensorIInputStreamBus();
        Mockito.when(ac.getBean(FlowSensorIInputStreamBus.class)).thenReturn(slm);

        JsonNode deviceIdMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("deviceId"))).thenReturn(deviceIdMock);
        Mockito.when(deviceIdMock.asText()).thenReturn("deviceId");

        JsonNode messageTypeMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("messageType"))).thenReturn(messageTypeMock);
        Mockito.when(messageTypeMock.asText()).thenReturn("ANALOG");

        JsonNode codeJsonNode = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("code"))).thenReturn(codeJsonNode);
        Mockito.when(codeJsonNode.asText()).thenReturn("result=true");

        Node root = ListeningSensorNode.create("id", globalContext, jn, ac);
        Node executeIfNode = ExecuteCodeNode.create("id1", globalContext, jn, ac);
        NodeMock nodeEnd = Mockito.mock(NodeMock.class);

        CountDownLatch latch = new CountDownLatch(1);
        Mockito.doAnswer(_ -> {
            latch.countDown();
            return null;

        }).when(nodeEnd).fireChildNodes(Mockito.any());

        FlowsBuilder.root(root)
                .add(root, executeIfNode)
                .add(executeIfNode, nodeEnd);

        ((NodeStart) root).config(flowContext);

        slm.takeBroadcaster(null).write(flowSensorAnalogI);

        assertTrue(latch.await(2, TimeUnit.SECONDS), "Test did not complete in time");
        Mockito.verify(nodeEnd, Mockito.times(1)).fireChildNodes(Mockito.any());
    }


    @Test
    void js_condition_false_variable_test() throws Exception {

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        Mockito.when(memberMock.getId()).thenReturn("memberId");

        FlowSensorIInputStreamBus slm = new FlowSensorIInputStreamBus();
        Mockito.when(ac.getBean(FlowSensorIInputStreamBus.class)).thenReturn(slm);

        JsonNode deviceIdMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("deviceId"))).thenReturn(deviceIdMock);
        Mockito.when(deviceIdMock.asText()).thenReturn("deviceId");

        JsonNode messageTypeMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("messageType"))).thenReturn(messageTypeMock);
        Mockito.when(messageTypeMock.asText()).thenReturn("DEVICE_CONNECTED");

        JsonNode codeJsonNode = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("code"))).thenReturn(codeJsonNode);
        Mockito.when(codeJsonNode.asText()).thenReturn("result = g_c.my_var");

        globalContext.getVariables().put("my_var", false);

        Node root = ListeningSensorNode.create("id", globalContext, jn, ac);
        Node executeIfNode = ExecuteCodeNode.create("id1", globalContext, jn, ac);
        NodeMock nodeEnd = Mockito.mock(NodeMock.class);
        CountDownLatch latch = new CountDownLatch(1);
        slm.addConsumer((s, f) -> latch.countDown());
        FlowsBuilder.root(root)
                .add(root, executeIfNode)
                .add(executeIfNode, nodeEnd);

        ((NodeStart) root).config(flowContext);
        slm.takeBroadcaster(null).write(flowSensorAnalogI);
        latch.await(5, TimeUnit.SECONDS);
        Thread.sleep(20);

        Mockito.verify(nodeEnd, Mockito.times(0)).fireChildNodes(Mockito.any());
    }

    @Test
    void js_override_java_variable() throws Exception {

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        Mockito.when(memberMock.getId()).thenReturn("memberId");

        FlowSensorIInputStreamBus slm = new FlowSensorIInputStreamBus();
        Mockito.when(ac.getBean(FlowSensorIInputStreamBus.class)).thenReturn(slm);

        JsonNode deviceIdMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("deviceId"))).thenReturn(deviceIdMock);
        Mockito.when(deviceIdMock.asText()).thenReturn("deviceId");

        JsonNode messageTypeMock = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("messageType"))).thenReturn(messageTypeMock);
        Mockito.when(messageTypeMock.asText()).thenReturn("ANALOG");

        JsonNode codeJsonNode = Mockito.mock(JsonNode.class);
        Mockito.when(jn.findValue(Mockito.same("code"))).thenReturn(codeJsonNode);
        Mockito.when(codeJsonNode.asText()).thenReturn("""
                g_c.my_var=12
                g_c.my_var_new=18
                l_c.new_value="test"
                let my_var_1=32
                result=true
                """);

        globalContext.getVariables().put("my_var", true);

        Node root = ListeningSensorNode.create("id", globalContext, jn, ac);
        Node executeCodeNode = ExecuteCodeNode.create("id1", globalContext, jn, ac);
        NodeMock nodeEnd = Mockito.mock(NodeMock.class);

        CountDownLatch latch = new CountDownLatch(1);
        Mockito.doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(nodeEnd).fireChildNodes(Mockito.any());


        FlowsBuilder.root(root)
                .add(root, executeCodeNode)
                .add(executeCodeNode, nodeEnd);

        Assertions.assertEquals(true, globalContext.getVariable("my_var"));
        ((NodeStart) root).config(flowContext);
        slm.takeBroadcaster("TEST").write(flowSensorAnalogI);

        assertTrue(latch.await(2, TimeUnit.SECONDS), "Test did not complete in time");

        Mockito.verify(nodeEnd, Mockito.times(1)).fireChildNodes(Mockito.any());
        Assertions.assertEquals(12, globalContext.getVariable("my_var"));

        Mockito.verify(nodeEnd, Mockito.times(1)).fireChildNodes(Mockito.any());
        Assertions.assertEquals(18, globalContext.getVariable("my_var_new"));
    }

}