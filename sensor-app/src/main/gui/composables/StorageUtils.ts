import {configUtils} from "./ConfigUtils";
import {useCookie} from "nuxt/app";

export interface UserInfo {
    fullName: string
    id: string,
    roles: Array<string>
}

export const storageUtils = (runtimeConfig: any) => {
    const {cookieApp} = configUtils(runtimeConfig);

    const age: number = 86400;
    const tokenCookie = useCookie("token", {
        maxAge: age,
        expires: new Date(new Date().getTime() + age * 1000),
        secure: false, // Ustaw true, jeśli używasz HTTPS
        httpOnly: false, // Ustaw true, aby zapobiec dostępowi do cookie przez JavaScript
        sameSite: 'lax', // Opcje: 'lax', 'strict', 'none'
    });

    const userCookie = useCookie("user", {
        maxAge: age,
        expires: new Date(new Date().getTime() + age * 1000)
    });


    const cookieAppSession = useCookie(cookieApp);
    const clear = () => {
        tokenCookie.value = null;
        userCookie.value = null;
    }
    const setUser = (user: UserInfo) => {
        userCookie.value = JSON.stringify(user);
    }

    const setToken = (token: string) => {
        tokenCookie.value = token;
    }

    const getToken = (): null | string => {
        const token = tokenCookie.value
        if (token == null) {
            return null;
        }
        if (token.length < 10) {
            return null;
        }

        return token;
    };


    const getUser = (): null | UserInfo => {
        const user = userCookie.value;

        if (user == null) {
            return null;
        }

        if (user.length < 10) {
            return null;
        }
        try {
            return JSON.parse(JSON.stringify(user));
        } catch (e) {
            return null;
        }
    };


    const getCookieApp = (): null | string => {
        const cookieAppId = cookieAppSession.value
        if (!cookieAppId) {
            return null;
        }
        if (cookieAppId.length < 5) {
            return null;
        }
        return cookieAppId;
    };


    return {
        setUser, clear, setToken, getToken, getUser, getCookieApp
    }
}