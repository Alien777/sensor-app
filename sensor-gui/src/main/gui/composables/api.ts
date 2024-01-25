export const api = (runtimeConfig: any) => {
    let {fetchApi} = fetchUtils(runtimeConfig);
    const getDrivers = async () => {
        return await fetchApi("/device", {method: 'get'})
            .then(value => value.data._rawValue);
    }
    return {
        getDrivers
    }
}
