import dayjs from "dayjs";

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