import dayjs from "dayjs";

export interface ErrorT {
    code: string;
    message: string;

}

export interface DeviceT {
    id: number;
    name: string;
    deviceKey: string;
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