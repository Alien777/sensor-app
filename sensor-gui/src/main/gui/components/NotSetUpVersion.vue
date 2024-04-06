<script setup lang="ts">
import {defineProps} from "vue";
import type {DeviceT} from "~/composables/api/StructureApp";

const props = defineProps({
  device: {
    type: Object as () => DeviceT,
    required: true
  }
});

const runtimeConfig = useRuntimeConfig();
const {memberId} = authUtils(runtimeConfig);
</script>

<template>
  <h6>Configuration of device is not available because didn't occur first connection.</h6>
  <p>This configuration please entry into device panel configuration.</p>
  <p><em>Member ID: </em>{{ memberId() }}</p>
  <p><em>Token: </em>{{ props.device.token }}</p>
</template>

<style scoped lang="css">

</style>