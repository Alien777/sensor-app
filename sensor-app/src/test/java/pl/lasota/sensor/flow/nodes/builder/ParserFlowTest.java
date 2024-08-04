package pl.lasota.sensor.flow.nodes.builder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import pl.lasota.sensor.device.DeviceApiInterface;
import pl.lasota.sensor.device.DeviceConfigInterface;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.builder.ParserFlows;
import pl.lasota.sensor.flow.services.nodes.nodes.AsyncNode;
import pl.lasota.sensor.flow.services.nodes.nodes.ExecuteCodeNode;
import pl.lasota.sensor.flow.services.nodes.nodes.ListeningSensorNode;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.member.services.MemberLoginService;
import pl.lasota.sensor.configs.properties.FlowsProperties;

import java.util.Collections;
import java.util.List;

class ParserFlowTest {
    @Mock
    private ApplicationContext ac;

    @Mock
    private FlowsProperties fp;

    @Mock
    private GlobalContext gc;

    @Mock
    private DeviceApiInterface saeMock;

    @Mock
    private DeviceConfigInterface dciMock;

    @Mock
    private DeviceSendMessageInterface dsmiMock;

    @Mock
    private MemberLoginService msMock;

    @Mock
    private Member memberMock;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(fp.getScanNodes()).thenReturn(new String[]{"pl.lasota.sensor.flow.services.nodes.nodes"});
    }

    @Test
    public void root_flow_test() {
        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);

        List<Node> root = new ParserFlows(fp, ac).flows("""
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
                """, gc);

        Assertions.assertInstanceOf(ListeningSensorNode.class, root.get(0));
    }


    @Test
    public void flow_1_test() {

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        List<Node> root = new ParserFlows(fp, ac).flows("""
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
                """, gc);

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
        Mockito.when(ac.getBean(DeviceConfigInterface.class)).thenReturn(dciMock);
        Mockito.when(dciMock.getConfigPwmPins(Mockito.any())).thenReturn(Collections.singletonList(23));

        List<Node> root = new ParserFlows(fp, ac).flows("""
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
                """, gc);

    }


    @Test
    public void flow_4_test() {

        Mockito.when(msMock.loggedMember()).thenReturn(memberMock);
        Mockito.when(ac.getBean(DeviceConfigInterface.class)).thenReturn(dciMock);
        Mockito.when(dciMock.getConfigPwmPins(Mockito.any())).thenReturn(List.of(23));
        Mockito.when(dciMock.getConfigDigitalPins(Mockito.any())).thenReturn(List.of(22));

        List<Node> root = new ParserFlows(fp, ac).flows("""
                {
                  "nodes": [
                    {
                      "ref": "VoiceFireCommendNode_7",
                      "name": "VoiceFireCommendNode",
                      "type": "input",
                      "sensor": {
                        "commends": "Zamknij"
                      },
                      "childed": [
                        "ExecuteCodeNode_8"
                      ],
                      "position": {
                        "x": -302,
                        "y": -128
                      }
                    },
                    {
                      "ref": "ExecuteCodeNode_8",
                      "name": "ExecuteCodeNode",
                      "type": "default",
                      "sensor": {
                        "code": "let result=true\\ng_c.pwm=1000\\ng_c.duration=800\\ng_c.digit=1"
                      },
                      "childed": [
                        "SleepNode_9"
                      ],
                      "position": {
                        "x": -199,
                        "y": 12
                      }
                    },
                    {
                      "ref": "SleepNode_9",
                      "name": "SleepNode",
                      "type": "default",
                      "sensor": {
                        "sleepTime": "500"
                      },
                      "childed": [
                        "SendDigitalValueNode_10"
                      ],
                      "position": {
                        "x": 139,
                        "y": 104
                      }
                    },
                    {
                      "ref": "SendDigitalValueNode_10",
                      "name": "SendDigitalValueNode",
                      "type": "default",
                      "sensor": {
                        "pin": 22,
                        "deviceId": "device",
                        "valueVariable": "g_c.digit"
                      },
                      "childed": [
                        "SendPwmValueNode_11"
                      ],
                      "position": {
                        "x": 439,
                        "y": 329
                      }
                    },
                    {
                      "ref": "SendPwmValueNode_11",
                      "name": "SendPwmValueNode",
                      "type": "default",
                      "sensor": {
                        "pin": 23,
                        "deviceId": "device",
                        "valueVariable": "g_c.duty",
                        "durationVariable": "g_c.duration"
                      },
                      "childed": [
                        "SleepNode_9"
                      ],
                      "position": {
                        "x": -140.79999999999995,
                        "y": 488.1999999999998
                      }
                    }
                  ],
                  "viewport": {
                    "x": 292,
                    "y": -85.19999999999982,
                    "zoom": 1
                  }
                }
                """, gc);

        System.out.println(root);

    }


}