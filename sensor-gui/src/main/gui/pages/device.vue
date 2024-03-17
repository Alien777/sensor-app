<template>
  <q-splitter v-model="splitterModel" unit="px">
    <template v-slot:before>
      <q-tabs
          vertical
          v-model="innerTab"
          class="text-teal">
        <q-tab v-for="device in devices" :name="device.id" icon="sensors"
               :label="device.name?device.name:device.id"/>
      </q-tabs>
    </template>
    <template v-slot:after>
      <q-tab-panels
          vertical
          v-model="innerTab">
        <q-tab-panel v-for="device in devices" :name="device.id" icon="sensors">
          <AdvencedConfigurationDevice :device="device"/>
        </q-tab-panel>
      </q-tab-panels>
    </template>
  </q-splitter>
</template>

<script setup lang="ts">
import {deviceApi} from "~/composables/api/DeviceApi";
import {ref, onMounted} from 'vue'


const innerTab = ref('')
const splitterModel = ref(200)

const runtimeConfig = useRuntimeConfig();
const {getAllDevice} = deviceApi(runtimeConfig);

const {data: devices} = useAsyncData('devices', getAllDevice);
onMounted(() => {
  setTimeout(() => {
    if (devices && devices.value && devices.value?.length >= 1) {
      innerTab.value = devices.value[0].id;
    }

  }, 100)
})
definePageMeta({
  layout: "panel",
  middleware: "page-any-roles"
})

</script>
<style scoped lang="css">

</style>