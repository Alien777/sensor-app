import dayjs from "dayjs";
import {ref} from "vue";
import AnalogReadSetUpNode from "~/components/flows/nodes/AnalogReadSetUpNode.vue";
import DigitalSetUpNode from "~/components/flows/nodes/DigitalSetUpNode.vue";
import PwmWriteSetUpNode from "~/components/flows/nodes/PwmWriteSetUpNode.vue";
import ListeningSensorNode from "~/components/flows/nodes/ListeningSensorNode.vue";
import CronNode from "~/components/flows/nodes/CronNode.vue";
import FireOnceNode from "~/components/flows/nodes/FireOnceNode.vue";
import StopCurrentProcessFlowNode from "~/components/flows/nodes/StopCurrentProcessFlowNode.vue";
import SleepNode from "~/components/flows/nodes/SleepNode.vue";
import AsyncNode from "~/components/flows/nodes/AsyncNode.vue";
import ExecuteCodeNode from "~/components/flows/nodes/ExecuteCodeNode.vue";
import RequestAnalogDataNode from "~/components/flows/nodes/RequestAnalogDataNode.vue";
import SendDigitalValueNode from "~/components/flows/nodes/SendDigitalValueNode.vue";
import SendPwmValueNode from "~/components/flows/nodes/SendPwmValueNode.vue";

export enum StatusOfFlow {
    OK = "OK",
    NOT_FOUND = "NOT_FOUND",
    ERROR = "ERROR",
    IS_ACTIVE_ALREADY = "IS_ACTIVE_ALREADY"
}

export interface ConfigNode {
    nodes: Array<Node>
    viewport: any;
}

export interface Node {
    ref: string;
    name: string;
    childed: Array<string>;
    sensor: any;
    type: string,
    position: any;
}

export interface NodeDraggable {
    name: string;
    type: string;
    sensor: any;
    readableName: string;
    description: string;
}

export interface FlowT {
    id: number;
    name: string;
    config: string;
    isActivate: boolean;
}

export interface ErrorT {
    code: string;
    message: string;
}

export interface DeviceT {
    id: string;
    name: string;
    version: string;
    token: string;
    hasConfig: boolean
}

export interface DeviceConfigT {
    id: number;
    config: string;
    schema: string;
    forVersion: string;
    isCorrect: boolean;
    time: Date | string;
}

export interface DeviceConfigSaveT {
    config: string;
    version: string;
}

export interface DeviceSaveT {
    token: string
    server: string;
}

export function timeToDate(d: DeviceConfigT): DeviceConfigT {
    return {
        ...d,
        time: new Date(d.time)
    };
}

export function formatTime(time: Date) {
    return dayjs(time).format("HH:mm:ss DD-mm-YYYY")
}

export const draggableItems = [
    {
        type: 'default',
        name: 'AnalogReadSetUpNode',
        component: AnalogReadSetUpNode,
        readableName: "Analog Set up",
        description: "setup analog pin"
    },
    {
        type: 'default',
        name: 'DigitalSetUpNode',
        component: DigitalSetUpNode,
        readableName: "Digital Set up",
        description: "setup digital pin"
    },
    {
        type: 'default',
        name: 'PwmWriteSetUpNode',
        component: PwmWriteSetUpNode,
        readableName: "Pwm Set up",
        description: "setup pwm pin"
    },
    {
        type: 'input',
        name: 'ListeningSensorNode',
        component: ListeningSensorNode,
        readableName: "Listening events",
        description: "fires up when it receives a message"
    },
    {type: 'input', name: 'CronNode', component: CronNode, readableName: "Cron executor"},
    {
        type: 'input',
        name: 'FireOnceNode',
        component: FireOnceNode,
        readableName: "Fire once",
        description: "Node important for api fire-once"
    },
    {
        type: 'input',
        name: 'VoiceFireCommendNode',
        readableName: "Run flow by voice commend",
        description: "Run commend by voice"
    },
    {type: 'default', name: 'SendPwmValueNode', component: SendPwmValueNode, readableName: "Send PWM to Device"},
    {
        type: 'default',
        name: 'SendDigitalValueNode',
        component: SendDigitalValueNode,
        readableName: "Send value as for digital input"
    },
    {
        type: 'default',
        name: 'RequestAnalogDataNode',
        component: RequestAnalogDataNode,
        readableName: "Analog data query"
    },
    {type: 'default', name: 'ExecuteCodeNode', component: ExecuteCodeNode, readableName: "Custom code"},
    {type: 'default', name: 'AsyncNode', component: AsyncNode, readableName: "Execute as none block flow"},
    {type: 'default', name: 'SleepNode', component: SleepNode, readableName: "Sleep flow"},
    {
        type: 'default',
        name: 'StopCurrentProcessFlowNode',
        component: StopCurrentProcessFlowNode,
        readableName: "Restart flow"
    }
];