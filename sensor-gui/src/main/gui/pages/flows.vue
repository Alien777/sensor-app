<template>
  <q-splitter v-model="width" unit="px">
    <template v-slot:before>
      <q-tabs
          vertical
          v-model="tab"
          class="text-teal">
        <q-tab :name="'new'" icon="schema"
               label="Create New Flow"/>
        <q-tab v-for="flow in flows" :style="!flow.isActivate?'color: red':''" :name="flow.id"
               :icon="flow.isActivate?'start':'stop'"
               :label="flow.name?flow.name:flow.id"/>
      </q-tabs>
    </template>
    <template v-slot:after>
      <q-tab-panels
          vertical
          v-model="tab">
        <q-tab-panel :name="'new'" icon="schema">
          <FlowEditor :flow="null"/>
        </q-tab-panel>
        <q-tab-panel v-for="flow in flows" :name="flow.id" icon="schema">
          <FlowEditor :flow="flow"/>
        </q-tab-panel>
      </q-tab-panels>
    </template>
  </q-splitter>
</template>

<script setup lang="ts">

import {flowApi} from "~/composables/api/FlowApi";
import {onMounted, ref} from 'vue'
import FlowEditor from "~/components/flows/FlowEditor.vue";

definePageMeta({
  layout: "panel",
  middleware: "page-any-roles"
})

const runtimeConfig = useRuntimeConfig();
const {getAll} = flowApi(runtimeConfig)
const {data: flows} = useAsyncData('flows', getAll);

const tab = ref('')
const width = ref(200)


onMounted(() => {
  setTimeout(() => {
    if (flows && flows.value && flows.value?.length >= 1) {
      tab.value = flows.value[0].id;
    }
  }, 100)
})


</script>

