<template>
  <el-container class="page-container">
    <navbar/>
    <el-main class="el-main">
      <slot/>
    </el-main>
    <el-footer class="footer">Footer
    </el-footer>
  </el-container>
</template>

<style scoped>
.page-container {
  display: flex;
  min-height: 100vh;
}

.el-main {
  background-color: #f5f5f5;
  --el-main-padding: 0px;
  padding-top: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.footer {
  color: white;
  padding: 10px 20px;
  text-align: center;
}
</style>
<script setup lang="ts">

import Navbar from "../components/forsite/navbar.vue";
</script>