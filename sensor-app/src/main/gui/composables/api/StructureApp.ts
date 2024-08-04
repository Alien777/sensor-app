import dayjs from "dayjs";
import {ref} from "vue";

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

export const draggableItems = ref([
    {type: 'input', name: 'ListeningSensorNode', readableName: "Listening events", description: "fires up when it receives a message"},
    {type: 'input', name: 'CronNode', readableName: "Cron executor"},
    {type: 'input', name: 'FireOnceNode', readableName: "Fire once", description: "Node important for api fire-once"},
    {type: 'input', name: 'VoiceFireCommendNode', readableName: "Run flow by voice commend", description: "Run commend by voice"},
    {type: 'default', name: 'SendPwmValueNode', readableName: "Send PWM to Device"},
    {type: 'default', name: 'SendDigitalValueNode', readableName: "Send value as for digital input"},
    {type: 'default', name: 'RequestAnalogDataNode', readableName: "Analog data query"},
    {type: 'default', name: 'ExecuteCodeNode', readableName: "Custom code"},
    {type: 'default', name: 'AsyncNode', readableName: "Execute as none block flow"},
    {type: 'default', name: 'SleepNode', readableName: "Sleep flow"},
    {type: 'default', name: 'StopCurrentProcessFlowNode', readableName: "Restart flow"}
]);