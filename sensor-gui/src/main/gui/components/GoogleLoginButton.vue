<template>
  <a v-if="!isAuth()" :href="`${apiHost}/oauth2/authorization/google`">

    <QBtn label="Zaloguj przez Google" icon="pi pi-google"
            severity="danger" text/>
  </a>
  <RouterLink v-else to="/panel">
    <QBtn severity="danger" label="Idź do panelu" icon="pi pi-circle-fill">
    </QBtn>
  </RouterLink>
</template>

<script setup lang="ts">
const runtimeConfig = useRuntimeConfig();
const {isAuth} = authUtils(runtimeConfig);

const {apiHost} = configUtils(runtimeConfig)
</script>