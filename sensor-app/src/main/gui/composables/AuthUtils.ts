import {storageUtils, type UserInfo} from "~/composables/StorageUtils";
import {simpleAuth} from "~/composables/ClientLogout";
import {fetchUtils} from "./FetchUtils";
import {navigateTo} from "nuxt/app";

export const authUtils = (runtimeConfig: any) => {
    let {fetchApi} = fetchUtils(runtimeConfig);
    let {simpleLogout} = simpleAuth(runtimeConfig);
    const {setToken, setUser, clear, getToken, getUser, getCookieApp} = storageUtils(runtimeConfig);

    const logout = () => {
        fetchApi('/auth/logout', getToken(), {method: 'delete'}).finally(() => simpleLogout())
    }

    const mainAuthProcess = async () => {
        const navigate = await authProcess();
        if (navigate) {
            return navigate;
        }
        return null;
    }

    const authProcess = async () => {
        if (process.server) {
            const isError = await processServerAuth();
            if (isError) {
                clear();
                return navigateTo('/auth-error');
            }
            return null;
        }
        if (process.client) {
            const isAuthenticated = await processClientAuth()
            if (!isAuthenticated) {
                return navigateTo('/auth-error')
            } else {
                return navigateTo('/panel')
            }
        }
        return null;
    }

    const processServerAuth = async () => {
        return !getCookieApp();
    }

    const processClientAuth = async () => {
        if (isAuth()) {
            return true;
        }
        try {
            const r = await fetchApi(`/auth/token`, getToken(), {method: 'get'});
            if (r.error.value) {
                return;
            }
            const token = r.data.value as string;
            setToken(token);
            const u = await fetchApi('/auth/user-details', token, {method: 'get'});
            if (u.error.value) {
                return;
            }
            let user = u.data.value as UserInfo;
            setUser(user);
            return true;
        } catch (error) {
            return false;
        }

    }
    const isAuth = (): boolean => {
        if (process.server) {
            const isAuth = getUser() != null && getToken() != null && getCookieApp() != null;
            if (!isAuth) {
                clear();
            }
            return isAuth;
        } else if (process.client) {
            return getUser() != null && getToken() != null;
        } else {
            return false;
        }
    }

    const username = () => {
        return getUser()?.fullName as string;
    }

    const memberId = () => {
        return getUser()?.id as string;
    }

    const hasRole = (role: string) => {
        const user = getUser();
        return !!(user && user.roles && user.roles.includes(role));
    };

    const basicLogin = (username: string, password: string) => {
        fetchApi('/auth/login', null, {
            method: 'POST',
            headers: {
                Authorization: `Basic ${btoa(username + ':' + password)}`
            }
        }).then((re) => {
            if(re.error.value)
            {
                navigateTo("/auth-error");
            }else {
                navigateTo("/oauth2");
            }
        })
    }


    return {
        auth: mainAuthProcess,
        hasRole,
        username,
        memberId,
        isAuth,
        basicLogin,
        logout
    }
}