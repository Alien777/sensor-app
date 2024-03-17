export const configUtilsApi = (runtimeConfig: any) => {
    let {fetchApiRequest} = fetchUtils(runtimeConfig);
    const getPwmsPins = async (deviceId: string): Promise<number[]> => {
        let value = await fetchApiRequest<number[]>(`/config-utils/${deviceId}/pwm/pins`,
            {method: 'get'});
        return value.data.value;
    }
    const getAnalogsPins = async (deviceId: string): Promise<number[]> => {
        let value = await fetchApiRequest<number[]>(`/config-utils/${deviceId}/analog/pins`,
            {method: 'get'});
        return value.data.value;
    }

    const getMessageTypes = async (deviceId: string): Promise<number[]> => {
        let value = await fetchApiRequest<number[]>(`/config-utils/${deviceId}/message-type`,
            {method: 'get'});
        return value.data.value;
    }


    return {
        getPwmsPins, getAnalogsPins, getMessageTypes
    }
}


