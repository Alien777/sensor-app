<script setup lang="ts">
import MicRecover from "~/components/forsite/mic-recover.vue";

const runtimeConfig = useRuntimeConfig();
const {username, logout, isAuth} = authUtils(runtimeConfig);
</script>

<template>
  <el-header class="navbar">
    <div class="navbar-left">
      <el-menu router mode="horizontal">
        <el-menu-item v-if="!isAuth()" index="1" :route="{ path: '/' }">Strona główna</el-menu-item>
        <el-menu-item v-if="isAuth()" index="2" :route="{ path: '/device' }">Urządzenia</el-menu-item>
        <el-menu-item v-if="isAuth()" index="2" :route="{ path: '/flows' }">Flow</el-menu-item>
      </el-menu>
    </div>
    <div  v-if="isAuth()" class="navbar-right">
      <el-menu mode="horizontal">
        <el-menu-item index="1" :route="{ path: '/' }">
          <mic-recover/>
        </el-menu-item>
        <el-menu-item index="2" @click="logout()">Logout</el-menu-item>
      </el-menu>
    </div>
  </el-header>
</template>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  --el-header-padding: 0 0px;
}

.navbar-left {
  flex: 1;
}

.navbar-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}
</style>
