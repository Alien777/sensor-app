<template>
  <q-splitter v-model="splitterModel" unit="px">
    <template v-slot:before>
      <q-tabs
          vertical
          v-model="innerTab"
          class="text-teal">
        <q-tab name="newDevice" style="color: green" icon="add">
          <p>Add new device</p>
        </q-tab>
        <q-tab v-for="device in devices" :name="device.id" :icon="device.version?'sensors':'autorenew'">
          <div style="color: green" v-if="device.version">
            <p>id:<em> {{ device.id }}</em></p>
            <p>{{ device.name }}</p>
          </div>
          <div v-else style="color: #252526">
            <p>{{ device.name }}</p>
          </div>
        </q-tab>
      </q-tabs>
    </template>
    <template v-slot:after>
      <q-tab-panels
          vertical
          v-model="innerTab">
        <q-tab-panel v-for="device in devices" :name="device.id" icon="not_">
          <AdvencedConfigurationDevice  :onChange="onChange" :device="device"/>
        </q-tab-panel>
        <q-tab-panel name="newDevice">
          <NewDevice :onChange="onChange"></NewDevice>
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
const {data: devices, refresh} = useAsyncData('devices', getAllDevice);
const onChange = () => {
  refresh();
}

onMounted(() => {
  setTimeout(() => {
    if (devices && devices.value && devices.value?.length >= 1) {
      innerTab.value = devices.value[0].id;
    } else {
      innerTab.value = "newDevice"
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