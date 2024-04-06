<template>
  <div class="q-img q-img--menu absolute-top" role="img" style="height: 204px;">
    <div style="padding-bottom: 82.5195%;"></div>
    <div class="q-img__container absolute-full"><img
        class="q-img__image q-img__image--with-transition q-img__image--loaded" loading="lazy" fetchpriority="auto"
        aria-hidden="true" draggable="false" src="https://cdn.quasar.dev/img/material.png"
        style="object-fit: cover; object-position: 50% 50%;"></div>
    <div class="q-img__content absolute-full q-anchor--skip">
      <div class="absolute-bottom bg-transparent">
        <div class="q-avatar q-mb-sm" style="font-size: 56px;">
          <div class="q-avatar__content row flex-center overflow-hidden"><img
              src="https://cdn.quasar.dev/img/boy-avatar.png"></div>
        </div>
        <div class="text-weight-bold">{{ username() }}</div>
        <q-btn @click="logout()" icon="logout" label="Logout"/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const runtimeConfig = useRuntimeConfig();
const {isAuth, username, memberId, logout} = authUtils(runtimeConfig);

const {apiHost} = configUtils(runtimeConfig)
</script>