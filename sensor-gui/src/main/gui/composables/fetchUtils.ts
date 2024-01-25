import {type FetchResult, useCookie, useFetch, type UseFetchOptions,} from "#app";
import {FetchError} from "ofetch";
import type {AvailableRouterMethod, NitroFetchRequest} from "nitropack";

import type {KeysOf} from 'nuxt/dist/app/composables/asyncData';
import {storageUtils} from "~/composables/storageUtils";

export const fetchUtils = (runtimeConfig: any) => {


    const {getToken} = storageUtils(runtimeConfig);
    const {apiUrl, cookieApp} = configUtils(runtimeConfig);


    const sensorAppCookies = useCookie('SENSOR_APP_COOKIES');

    const prefixAuthorization = 'Bearer ';

    const fetchApi = <ResT = void, ErrorT = FetchError, ReqT extends NitroFetchRequest = NitroFetchRequest, Method extends AvailableRouterMethod<ReqT> = AvailableRouterMethod<ReqT>, _ResT = ResT extends void ? FetchResult<ReqT, Method> : ResT, DataT = _ResT, PickKeys extends KeysOf<DataT> = KeysOf<DataT>, DefaultT = null>(
        url: string,
        additionalOptions?: UseFetchOptions<_ResT, DataT, PickKeys, DefaultT, ReqT, Method>
    ) => {
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

    return {fetchApi};
}
