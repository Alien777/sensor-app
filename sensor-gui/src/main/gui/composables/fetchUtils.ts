import {FetchResult, useFetch, UseFetchOptions,} from "#app";
import {FetchError} from "ofetch";
import {AvailableRouterMethod, NitroFetchRequest} from "nitropack"; // Upewnij się, że ścieżka jest prawidłowa
import type {KeysOf} from 'nuxt/dist/app/composables/asyncData';
import {storageUtils} from "~/composables/storageUtils";

export const fetchUtils = () => {
    const {getToken} = storageUtils();
    const {apiUrl} = configUtils();
    const prefixAuthorization = 'Bearer ';

    const fetchApi = <ResT = void, ErrorT = FetchError, ReqT extends NitroFetchRequest = NitroFetchRequest, Method extends AvailableRouterMethod<ReqT> = AvailableRouterMethod<ReqT>, _ResT = ResT extends void ? FetchResult<ReqT, Method> : ResT, DataT = _ResT, PickKeys extends KeysOf<DataT> = KeysOf<DataT>, DefaultT = null>(
        url: string,
        additionalOptions?: UseFetchOptions<_ResT, DataT, PickKeys, DefaultT, ReqT, Method>
    ) => {
        const token = getToken();
        return useFetch(url, {
            baseURL: apiUrl,
            headers: {
                ...(token ? {'Authorization': prefixAuthorization + token} : {}),
                ...(additionalOptions?.headers || {}),
            },
            credentials: 'include',
            ...additionalOptions,
        });
    }


    return {fetchApi};
}
