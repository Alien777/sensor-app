import {
    type DeviceConfigSaveT,
    type DeviceConfigT,
    type DeviceSaveT,
    type DeviceT,
    timeToDate
} from "~/composables/api/StructureApp";
import {Notify} from "quasar";

export const deviceApi = (runtimeConfig: any) => {
    let {fetchApiRequest} = fetchUtils(runtimeConfig);

    const saveDevice = async (id: string, name: string): Promise<string> => {
        let value = await fetchApiRequest<string>(`/device`,
            {
                method: 'post', body: {
                    id: id,
                    name: name
                }
            });
        return value.data.value;
    }

    const initDevice = async (version: string, name: string, wifiSsid: string, wifiPassword: string, apPassword): Promise<boolean> => {
        try {
            let value = await fetchApiRequest<any>(`/device/build`, {
                method: 'post',
                body: {
                    version: version,
                    name: name,
                    wifiSsid: wifiSsid,
                    wifiPassword: wifiPassword,
                    apPassword: apPassword,
                },
                responseType: 'blob',
            });

            const blob = await value.data.value;
            if (blob) {
                const url = window.URL.createObjectURL(blob as Blob);
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', `${name}-${version}.zip`); // Nazwa pliku do pobrania
                document.body.appendChild(link);
                link.click();
                if (link.parentNode)
                    link.parentNode.removeChild(link);
                window.URL.revokeObjectURL(url);
                return true;
            } else {
                return false;
            }
        } catch (error) {
            console.error('Error during device initialization:', error);
            return false;
        }
    }

    const getAllDevice = async (withNotActive = false): Promise<DeviceT[]> => {
        let value = await fetchApiRequest<DeviceT[]>(`/device?withNotActive=${withNotActive}`,
            {method: 'get'});
        return value.data.value;
    }
    const getDeviceConfig = async (id: string): Promise<DeviceConfigT> => {
        let value = await fetchApiRequest<DeviceConfigT>(`/device/${id}/config`,
            {method: 'get'});
        return timeToDate(value.data.value);
    }


    const saveDeviceConfig = async (id: string, config: DeviceConfigSaveT): Promise<DeviceConfigT> => {
        let value = await fetchApiRequest<DeviceConfigT>(`/device/${id}/config`,
            {method: 'post', body: config});
        return value.data.value;
    }


    const getAllConfigs = async (id: string): Promise<DeviceConfigT[]> => {
        let value = await fetchApiRequest<DeviceConfigT[]>(`/device/${id}/config/version`,
            {method: 'get'});
        return value.data.value.map(value1 => timeToDate(value1));
    }

    const getVersions = async (): Promise<string[]> => {
        let value = await fetchApiRequest<string[]>(`/device/versions`,
            {method: 'get'});
        return value.data.value;
    }
    const activateConfig = async (id: string, id_config: number): Promise<any> => {
        return await fetchApiRequest<DeviceConfigT[]>(`/device/${id}/config/${id_config}/activate`,
            {method: 'put'});
    }

    return {
        getAllDevice,
        getDeviceConfig,
        saveDeviceConfig,
        getAllConfigs,
        activateConfig,
        saveDevice,
        initDevice,
        getVersions
    }


}


