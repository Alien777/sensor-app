<template xmlns="http://www.w3.org/1999/html">
  <div class="container">
    <el-tag>
      {{ props.name }}
    </el-tag>
    <el-button type="primary" style="margin-left: 16px" @click="drawer = true">
      open
    </el-button>
    <div style="display: flex; overflow: auto">
      <slot name="visable"/>
    </div>s
    <el-drawer :append-to-body="true" v-model="drawer" title="I'm outer Drawer" size="50%">


    </el-drawer>
  </div>
</template>
<script lang="ts" setup>
const drawer = ref(false)
const props = defineProps({
  name: {
    type: String,
    required: true,
  },
})

</script>
<style lang="css" scoped>

</style>