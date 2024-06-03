export default defineNuxtRouteMiddleware(async (to, from) => {
    const runtimeConfig = useRuntimeConfig();
    const {isAuth} = authUtils(runtimeConfig);
    const {simpleLogout} = simpleAuth(runtimeConfig);
    if (!isAuth()) {
        return simpleLogout();
    }
})

