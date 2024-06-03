<template>
  <q-splitter v-model="splitterModel" unit="px">
    <template v-slot:before>
      <q-tabs
          shrink
          vertical
          v-model="innerTab"
      >
        <q-tab class="bg-green-2" name="newDevice" icon="add">
          <p>Add new device</p>
        </q-tab>
        <q-tab :class="!device.version?'bg-red-1':'bg-white'" v-for="device in devices" :name="device.id"
               :icon="device.version?'sensors':'autorenew'">
          <div v-if="device.version">
            <p>{{ device.name.substring(0, 20) }}</p>
          </div>
          <div v-else>
            <p>{{ device.name.substring(0, 20) }}</p>
          </div>
        </q-tab>
      </q-tabs>
    </template>
    <template v-slot:after>
      <q-tab-panels
          vertical
          v-model="innerTab">
        <q-tab-panel v-for="device in devices" :name="device.id" icon="not_">
          <AdvencedConfigurationDevice :onChange="onChange" :device="device"/>
        </q-tab-panel>
        <q-tab-panel name="newDevice">
          <div class="text-h5"><p>Create new device</p></div>
          <hr>
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
const {data: devices, refresh} = useAsyncData('devices', () => getAllDevice(true));
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