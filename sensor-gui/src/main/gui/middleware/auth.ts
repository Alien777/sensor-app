export default defineNuxtRouteMiddleware(async (to, from) => {
    const {auth} = authUtils();
    const promise = await auth();
    if (promise != null) {
        return promise;
    }
})

