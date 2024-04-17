package pl.lasota.sensor.flows.nodes.builder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.nodes.AsyncNode;
import pl.lasota.sensor.flows.nodes.nodes.ExecuteCodeNode;
import pl.lasota.sensor.flows.nodes.nodes.ListeningSensorNode;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;
import pl.lasota.sensor.internal.apis.api.SensorMicroserviceEndpoint;
import pl.lasota.sensor.member.entities.Member;
import pl.lasota.sensor.member.services.MemberService;

import java.util.Collections;
import java.util.List;

class ParserFlowTest {

    private TaskScheduler ts;

    private SensorListeningManager slm;

    @Mock
    private SensorMicroserviceEndpoint saeMock;

    @Mock
    private MemberService msMock;

    @Mock
    private Member memberMock;

    private NodeCreatorFactory ncf;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ts = new SimpleAsyncTaskScheduler();
        slm = new SensorListeningManager();
        ncf = new NodeCreatorFactory(ts, slm, saeMock, msMock);
    }

    @Test
    public void root_flow_test() {
        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        List<Node> root = new ParserFlows().flows("""
                {
                "nodes": [
                {
                    "ref": "123df",
                    "name": "ListeningSensorNode",
                    "sensor":{
                       "deviceId": "device",
                       "memberId": "member",
                       "messageType": "ANALOG",
                       "pin": "1",
                       "valueVariable": "value"
                    },
                    "childed":[]
                }
                ],
                 "viewport": {
                    "x": 1098.2806938768847,
                    "y": 708.8455471415423,
                    "zoom": 0.7393413459089405
                  }
                }
                """, ncf.create());

        Assertions.assertInstanceOf(ListeningSensorNode.class, root.get(0));
    }


    @Test
    public void flow_1_test() {

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        List<Node> root = new ParserFlows().flows("""
                   {
                  "nodes": [
                    {
                    "ref": "root",
                    "name": "ListeningSensorNode",
                    "sensor":{
                       "deviceId": "device",
                       "memberId": "member",
                       "messageType": "ANALOG",
                       "pin": "1",
                       "valueVariable": "value"
                    },
                    "childed":["code_ref_1","if_ref_1"]
                },
                 {
                    "ref": "code_ref_1",
                    "name": "ExecuteCodeNode",
                    "sensor":{
                       "code": "let a=0"
                    },
                    "childed":["if_ref_1","async_ref_1"]
                },
                   {
                    "ref": "if_ref_1",
                    "name": "ExecuteCodeNode",
                    "sensor":{
                        "code": "0=0"
                    },
                    "childed":[]
                },
                   {
                    "ref": "async_ref_1",
                    "name": "AsyncNode",
                    "childed":[]
                }
                ],
                 "viewport": {
                    "x": 1098.2806938768847,
                    "y": 708.8455471415423,
                    "zoom": 0.7393413459089405
                  }
                }
                """, ncf.create());

        Assertions.assertInstanceOf(ListeningSensorNode.class, root.get(0));
        Assertions.assertInstanceOf(ExecuteCodeNode.class, root.get(0).getNodes().get(0));
        Assertions.assertInstanceOf(ExecuteCodeNode.class, root.get(0).getNodes().get(1));

        Node code_ref_1 = root.get(0).getNodes().get(0);
        Assertions.assertInstanceOf(ExecuteCodeNode.class, code_ref_1);
        Assertions.assertInstanceOf(AsyncNode.class, code_ref_1.getNodes().get(0));
        Assertions.assertInstanceOf(ExecuteCodeNode.class, code_ref_1.getNodes().get(1));
    }

    @Test
    public void flow_3_test() {

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        Mockito.when(saeMock.getConfigPwmPins(Mockito.any())).thenReturn(Collections.singletonList(23));
        List<Node> root = new ParserFlows().flows("""
                {
                  "nodes": [
                    {
                      "ref": "ListeningSensorNode_0",
                      "name": "ListeningSensorNode",
                      "type": "input",
                      "sensor": {
                        "deviceId": "DDDDDDDDDDDD",
                        "messageType": "DEVICE_CONNECTED"
                      },
                      "childed": [
                        "ExecuteCodeNode_0"
                      ],
                      "position": {
                        "x": -1096.8396134599664,
                        "y": -793.3699489454277
                      }
                    },
                    {
                      "ref": "ExecuteCodeNode_0",
                      "name": "ExecuteCodeNode",
                      "type": "default",
                      "sensor": {
                        "code": "let result=true\\nif(!g_c.value)\\n{\\ng_c.value=1;\\n}\\ng_c.value=g_c.value+1\\ng_c.pwm_value=4+g_c.value"
                      },
                      "childed": [
                        "SendPwmValueNode_0"
                      ],
                      "position": {
                        "x": -811.294868717533,
                        "y": -574.5838353251393
                      }
                    },
                    {
                      "ref": "SendPwmValueNode_0",
                      "name": "SendPwmValueNode",
                      "type": "default",
                      "sensor": {
                        "pin": 23,
                        "deviceId": "DDDDDDDDDDDD",
                        "valueVariable": "g_c.pwm_value"
                      },
                      "childed": [],
                      "position": {
                        "x": -1081.2361895814008,
                        "y": -293.96267253065946
                      }
                    },
                    {
                      "ref": "CronNode_0",
                      "name": "CronNode",
                      "type": "input",
                      "sensor": {
                        "cron": "*/30 * * * * *"
                      },
                      "childed": [
                        "ExecuteCodeNode_0"
                      ],
                      "position": {
                        "x": -567.5621236899894,
                        "y": -758.1676377806509
                      }
                    }
                  ],
                  "viewport": {
                    "x": 1098.2806938768847,
                    "y": 708.8455471415423,
                    "zoom": 0.7393413459089405
                  }
                }
                """, ncf.create());

    }
}