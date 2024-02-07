import {timeToDate, type DeviceConfigT, type DeviceT, type DeviceConfigSaveT} from "~/composables/api/StructureApp";

export const deviceApi = (runtimeConfig: any) => {
    let {fetchApiRequest} = fetchUtils(runtimeConfig);
    const getAllDevice = async (): Promise<DeviceT[]> => {
        let value = await fetchApiRequest<DeviceT[]>("/device",
            {method: 'get'});
        return value.data.value;
    }
    const getDeviceConfig = async (id: number): Promise<DeviceConfigT> => {
        let value = await fetchApiRequest<DeviceConfigT>(`/device/${id}/config`,
            {method: 'get'});
        return timeToDate(value.data.value);
    }


    const saveDeviceConfig = async (id: number, config: DeviceConfigSaveT): Promise<DeviceConfigT> => {
        let value = await fetchApiRequest<DeviceConfigT>(`/device/${id}/config`,
            {method: 'post', body: config});
        return value.data.value;
    }


    const getAllConfigs = async (id: number): Promise<DeviceConfigT[]> => {
        let value = await fetchApiRequest<DeviceConfigT[]>(`/device/${id}/config/version`,
            {method: 'get'});
        return value.data.value.map(value1 => timeToDate(value1));
    }

    return {
        getAllDevice, getDeviceConfig, saveDeviceConfig, getAllConfigs
    }
}


