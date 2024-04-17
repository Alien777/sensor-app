import {useCookie, useFetch, type UseFetchOptions,} from "#app";
import {storageUtils} from "~/composables/StorageUtils";
import type {_AsyncData} from "#app/composables/asyncData";
import {FetchError} from "ofetch";
import {Notify} from 'quasar'
import {simpleAuth} from "~/composables/ClientLogout";
import type {ErrorT} from "~/composables/api/StructureApp";
import {configUtils} from "./ConfigUtils";

const prefixAuthorization = 'Bearer ';

export const fetchUtils = (runtimeConfig: any) => {

    let {simpleLogout} = simpleAuth(runtimeConfig);
    const {getToken} = storageUtils(runtimeConfig);
    const {apiUrl, cookieApp} = configUtils(runtimeConfig);
    const sensorAppCookies = useCookie('SENSOR_APP_COOKIES');
    const fetchApi = <T>(
        url: string,
        token: string | null,
        additionalOptions?: UseFetchOptions<any>,
    ): Promise<_AsyncData<T, FetchError<ErrorT> | null>> => {
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
        const data: _AsyncData<T, FetchError<ErrorT> | null> = await fetchApi(url, getToken(), additionalOptions);
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
                    message: `DETAILS: ${data.error.value.data?.message}`
                })
            }
        }

        return data;
    }

    return {fetchApi, fetchApiRequest};
}
