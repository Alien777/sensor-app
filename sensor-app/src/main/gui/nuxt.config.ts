import {defineNuxtConfig} from "nuxt/config";

export default defineNuxtConfig({
    devtools: {enabled: true},
    runtimeConfig: {
        public: {
            wsApi: 'ws://localhost:8080/api',
            apiHost: 'http://localhost:8080',
            apiUrl: 'http://localhost:8080/api',
        }
    },
    css: [
        "@vue-flow/core/dist/style.css",
        "@vue-flow/core/dist/theme-default.css",
    ],
    modules: [
        'dayjs-nuxt',
        'nuxt-monaco-editor',
        'nuxt-quasar-ui',
        'nuxt-socket-io',
    ],
    quasar: {plugins: ["Notify", "Dialog"]}
})