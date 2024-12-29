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
    // css: ['drawflow/dist/drawflow.min.css'],
    modules: [
        'dayjs-nuxt',
        'nuxt-socket-io',
        '@element-plus/nuxt',

    ],

    compatibilityDate: '2024-11-16',
})