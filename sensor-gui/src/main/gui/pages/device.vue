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
        <q-tab name="drivers" label="Drivers" />
      </q-tabs>

      <q-separator />

      <q-tab-panels v-model="tab" animated>
        <q-tab-panel name="drivers" class="q-pa-none">

          <q-splitter
              v-model="splitterModel"
          >

            <template v-slot:before>
              <q-tabs
                  v-model="innerTab"
                  vertical
                  class="text-teal"
              >
                <q-tab v-for="driver in data" :name="driver.id" icon="sensors" :label="driver.name" />
              </q-tabs>
            </template>

            <template v-slot:after>
              <q-tab-panels
                  v-model="innerTab"
                  animated
                  transition-prev="slide-down"
                  transition-next="slide-up"
              >
                <q-tab-panel v-for="driver in data" :name="driver.id" icon="mail" :label="driver.name" >
                  {{driver.id}}
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

import { ref } from 'vue'

const tab = ref('drivers')
const innerTab = ref('1')
const splitterModel = ref(20)

import {api} from "~/composables/api";
const runtimeConfig = useRuntimeConfig();
const {getDrivers} = api(runtimeConfig);

const {data, error} = await useAsyncData('drivers', getDrivers);

definePageMeta({
  layout: "panel"
})

</script>