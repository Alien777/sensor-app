export default defineNuxtRouteMiddleware(async (to, from) => {
    const runtimeConfig = useRuntimeConfig();
    const {auth} = authUtils(runtimeConfig);
    const promise = await auth();
    if (promise != null) {
        return promise;
    }
})

