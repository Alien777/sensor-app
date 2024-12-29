import { useCookie, useFetch, type UseFetchOptions } from "#app";
import { storageUtils } from "~/composables/StorageUtils";

import type { _AsyncData } from "#app/composables/asyncData";
import { FetchError } from "ofetch";
import { ElNotification } from "element-plus"; // Import Element Plus Notifications
import { simpleAuth } from "~/composables/ClientLogout";
import type { ErrorT } from "~/composables/api/StructureApp";
import { configUtils } from "./ConfigUtils";

const prefixAuthorization = "Bearer ";

export const fetchUtils = (runtimeConfig: any) => {
    const { simpleLogout } = simpleAuth(runtimeConfig);
    const { getToken } = storageUtils(runtimeConfig);
    const { apiUrl, cookieApp } = configUtils(runtimeConfig);
    const sensorAppCookies = useCookie("SENSOR_APP_COOKIES");

    const fetchApi = <T>(
        url: string,
        token: string | null,
        additionalOptions?: UseFetchOptions<any>
    ): Promise<_AsyncData<T, FetchError<ErrorT> | null>> => {
        const headers = {
            ...(token ? { Authorization: prefixAuthorization + token } : {}),
            ...(additionalOptions?.headers || {}),
            ...(process.server &&
            sensorAppCookies &&
            sensorAppCookies.value
                ? { Cookie: `${cookieApp}=${sensorAppCookies.value}` }
                : {}),
        };
        return useFetch(url, {
            baseURL: apiUrl,
            headers: headers,
            credentials: "include",
            ...additionalOptions,
        });
    };

    const fetchApiRequest = async <T>(
        url: string,
        additionalOptions?: UseFetchOptions<any>
    ): Promise<_AsyncData<T, FetchError<ErrorT> | null>> => {
        const data: _AsyncData<T, FetchError<ErrorT> | null> = await fetchApi(
            url,
            getToken(),
            additionalOptions
        );

        if (data.error.value != null) {
            const statusCode = data.error.value.statusCode;

            if (statusCode === 401) {
                simpleLogout();
            }

            if (statusCode === 403) {
                ElNotification({
                    type: "warning",
                    title: "Access Denied",
                    message: "You don't have permission.",
                    duration: 5000,
                });
            }

            if (statusCode === 500) {
                ElNotification({
                    type: "error",
                    title: "Server Error",
                    message: `DETAILS: ${data.error.value.data?.message}`,
                    duration: 5000,
                });
            }
        }

        return data;
    };

    return { fetchApi, fetchApiRequest };
};
