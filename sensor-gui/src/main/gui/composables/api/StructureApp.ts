import dayjs from "dayjs";

export interface Node {
    ref: string;
    name: string;
    childed: Array<string>;
    sensor: any;
    position: any;
}

export interface NodeDraggable {
    name: string;
    type: string;
}

export interface FlowT {
    id: number;
    config: string;
    name: string;
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

export function timeToDate(d: DeviceConfigT): DeviceConfigT {
    return {
        ...d,
        time: new Date(d.time)
    };
}

export function formatTime(time: Date) {
    return dayjs(time).format("HH:mm:ss DD-mm-YYYY")
}