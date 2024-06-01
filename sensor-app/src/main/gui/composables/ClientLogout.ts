import {storageUtils} from "~/composables/StorageUtils";
import {callWithNuxt} from "#app";
import {navigateTo, useNuxtApp} from "nuxt/app";

export const simpleAuth = (runtimeConfig: any) => {
    const {clear} = storageUtils(runtimeConfig);
    const app = useNuxtApp()
    const simpleLogout = () => {
        clear();
        return callWithNuxt(app, () => navigateTo('/'))
    }

    return {
        simpleLogout
    }
}