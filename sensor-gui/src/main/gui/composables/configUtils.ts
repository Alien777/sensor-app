export const configUtils = () => {
    const runtimeConfig = useRuntimeConfig()
    const apiUrl: string = runtimeConfig.public.apiUrl as string;
    const apiHost: string = runtimeConfig.public.apiHost as string;
    const cookieApp: string = 'SENSOR_APP_COOKIES' as string;
    return {
        apiUrl, apiHost, cookieApp
    }
}