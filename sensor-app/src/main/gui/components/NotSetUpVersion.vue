<script setup lang="ts">
import {defineProps} from "vue";
import type {DeviceT} from "~/composables/api/StructureApp";
import {resourceApi} from "~/composables/api/ResourcesApi";

const props = defineProps({
  device: {
    type: Object as () => DeviceT,
    required: true
  }
});

const runtimeConfig = useRuntimeConfig();
const {memberId} = authUtils(runtimeConfig);
const {mqttServer} = resourceApi(runtimeConfig);
const ip_server = ref('');
mqttServer().then(value => ip_server.value = value);
</script>

<template>
  <h6>Configuration of device is not available because didn't occur first connection.</h6>
  <p>This configuration please entry into device panel configuration.</p>
  <p>You can use downloaded file zip to configure this. If you connected device into USB</p>
  <p>Or you can connect with device by IP and put the values below</p>
  <p><em>Member ID: </em>{{ memberId() }}</p>
  <p><em>Token: </em>{{ props.device.token }}</p>
  <p><em>Server Ip: </em>{{ ip_server }}</p>
</template>

<style scoped lang="css">

</style>