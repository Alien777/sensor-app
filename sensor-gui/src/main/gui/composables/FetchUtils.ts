import {useCookie, useFetch, type UseFetchOptions,} from "#app";
import {storageUtils} from "~/composables/StorageUtils";
import type {_AsyncData} from "#app/composables/asyncData";
import {FetchError} from "ofetch";
import {Notify} from 'quasar'
import {simpleAuth} from "~/composables/ClientLogout";
import type {ErrorT} from "~/composables/api/StructureApp";

export const fetchUtils = (runtimeConfig: any) => {

    let {simpleLogout} = simpleAuth(runtimeConfig);
    const {getToken, clear} = storageUtils(runtimeConfig);
    const {apiUrl, cookieApp} = configUtils(runtimeConfig);
    const sensorAppCookies = useCookie('SENSOR_APP_COOKIES');
    const prefixAuthorization = 'Bearer ';

    const fetchApi = <T>(
        url: string,
        additionalOptions?: UseFetchOptions<any>
    ): Promise<_AsyncData<T, FetchError<ErrorT> | null>> => {
        const token = getToken();

        let headers = {
            ...(token ? {'Authorization': prefixAuthorization + token} : {}),
            ...(additionalOptions?.headers || {}),
            ...(process.server && sensorAppCookies && sensorAppCookies.value ? {'Cookie': `${cookieApp}=${sensorAppCookies.value}`} : {})
        };

        return useFetch(url, {
            baseURL: apiUrl,
            headers: headers,
            credentials: 'include',
            ...additionalOptions,
        });
    }

    const fetchApiRequest = async <T>(
        url: string,
        additionalOptions?: UseFetchOptions<any>
    ): Promise<_AsyncData<T, FetchError<ErrorT> | null>> => {
        const data: _AsyncData<T, FetchError<ErrorT> | null> = await fetchApi(url, additionalOptions);
        if (data.error.value != null) {
            if (data.error.value.statusCode === 401) {
                simpleLogout();
            }
            if (data.error.value.statusCode === 403) {
                Notify.create({
                    type: 'warning',
                    message: 'You don\'t has permission'
                })
            }
            if (data.error.value.statusCode === 500) {
                Notify.create({
                    type: 'warning',
                    message: `CODE: ${data.error.value.data?.code}, MESSAGE: ${data.error.value.data?.message}`
                })
            }
        }

        return data;
    }

    return {fetchApi, fetchApiRequest};
}
