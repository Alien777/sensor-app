import {defineNuxtConfig} from "nuxt/config";

export default defineNuxtConfig({

    runtimeConfig: {
        public: {
            apiUrl: 'http://localhost:8080/api',
            apiHost: 'http://localhost:8080',
        }
    },
    css: [
        "@vue-flow/core/dist/style.css",
        "@vue-flow/core/dist/theme-default.css",
    ],
    modules: [
        'dayjs-nuxt',
        'nuxt-monaco-editor',
        'nuxt-quasar-ui'
    ],
    quasar: {plugins: ["Notify", "Dialog"]}
})