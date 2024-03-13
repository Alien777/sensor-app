<template>
  <div class="q-pa-md">
    <q-card>
      <q-tabs
          v-model="tab"
          dense
          class="text-grey"
          active-color="primary"
          indicator-color="primary"
          align="justify"
      >
        <q-tab name="devices-config" label="Device config"/>
      </q-tabs>

      <q-separator/>

      <q-tab-panels v-model="tab" animated>
        <q-tab-panel name="devices-config" class="q-pa-none">

          <q-splitter v-model="splitterModel">
            <template v-slot:before>
              <q-tabs
                  v-model="innerTab"
                  vertical
                  class="text-teal">
                <q-tab v-for="device in devices" :name="device.id" icon="sensors"
                       :label="device.name?device.name:device.id"/>
              </q-tabs>
            </template>

            <template v-slot:after>
              <q-tab-panels
                  swipeable
                  v-model="innerTab"
                  animated
                  keep-alive
                  transition-prev="slide-down"
                  transition-next="slide-up">
                <q-tab-panel v-for="device in devices" :name="device.id" icon="sensors">
                  <AdvencedConfigurationDevice :device="device"/>
                </q-tab-panel>
              </q-tab-panels>
            </template>

          </q-splitter>
        </q-tab-panel>
      </q-tab-panels>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import {deviceApi} from "~/composables/api/DeviceApi";
import {ref} from 'vue'

const tab = ref('devices')

const innerTab = ref('1')
const splitterModel = ref(20)

const runtimeConfig = useRuntimeConfig();
const {getAllDevice} = deviceApi(runtimeConfig);

const {data: devices} = useAsyncData('devices', getAllDevice);

definePageMeta({
  layout: "panel",
  middleware: "page-any-roles"
})

</script>
<style scoped lang="css">

</style>