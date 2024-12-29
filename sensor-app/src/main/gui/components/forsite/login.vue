<template>
  <div v-if="!isAuth()">
    <!-- Google Login -->
    <div class="window-width row justify-center items-center">
      <el-card class="box-card" style="width: 400px;">
        <div>
          <a :href="`${apiHost}/oauth2/authorization/google`">
            <el-button type="danger" icon="el-icon-google">
              Login by Google
            </el-button>
          </a>
        </div>
      </el-card>
    </div>
    <br />
    <!-- Username and Password Login -->
    <div class="window-width row justify-center items-center">
      <el-card class="box-card" style="width: 400px;">
        <div>
          <el-input
              v-model="username"
              placeholder="Username"
              clearable
              prefix-icon="el-icon-user"
              class="mb-3"
          />
          <el-input
              v-model="password"
              placeholder="Password"
              show-password
              prefix-icon="el-icon-lock"
              class="mb-3"
          />
          <el-button
              type="success"
              icon="el-icon-unlock"
              @click="login"
          >
            Login
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
  <div v-else>
    <RouterLink to="/device">
      <el-button type="danger" icon="el-icon-document">
        Go to Panel
      </el-button>
    </RouterLink>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const runtimeConfig = useRuntimeConfig();
const { isAuth, basicLogin } = authUtils(runtimeConfig);
const { apiHost } = configUtils(runtimeConfig);

const username = ref('');
const password = ref('');

const login = () => {
  basicLogin(username.value, password.value);
};
</script>

<style scoped>
.window-width {
  width: 100%;
}
.mb-3 {
  margin-bottom: 1rem;
}
</style>
