export const resourceApi = (runtimeConfig: any) => {
    let {fetchApiRequest} = fetchUtils(runtimeConfig);

    const mqttServer = async (): Promise<string> => {
        let value = await fetchApiRequest<string>(`/resource/mqtt-server`,
            {
                method: 'get'
            });
        return value.data.value;
    }

    return {
        mqttServer
    }

}


