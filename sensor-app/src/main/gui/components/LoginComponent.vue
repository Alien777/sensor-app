<template>
  <div v-if="!isAuth()">
    <div class="window-width row justify-center items-center">
      <q-card style="width: 400px">
        <q-card-section>
          <a :href="`${apiHost}/oauth2/authorization/google`">
            <QBtn label="by Google" icon="login" color="red"/>
          </a>
        </q-card-section>
      </q-card>
    </div>
    <br>
    <div class="window-width row justify-center items-center">
      <q-card style="width: 400px">
        <q-card-section>
          <q-input label="Username" v-model="username"/>
          <q-input label="Password" type="password" v-model="password"/>
          <br>
          <q-btn label="Login" icon="login" color="green" @click="login"/>
        </q-card-section>
      </q-card>
    </div>
  </div>
  <div v-else>
    <RouterLink to="/panel">
      <QBtn severity="danger" label="Idź do panelu" icon="pi pi-circle-fill">
      </QBtn>
    </RouterLink>
  </div>

</template>

<script setup lang="ts">
const runtimeConfig = useRuntimeConfig();
const {isAuth, basicLogin} = authUtils(runtimeConfig);
const {apiHost} = configUtils(runtimeConfig)

import {ref} from 'vue'

const username = ref('')
const password = ref('')

const login = () => {
  basicLogin(username.value, password.value);
}
</script>