<template>
  <q-splitter v-model="width" unit="px">
    <template v-slot:before>
      <q-tabs
          vertical
          v-model="tab">
        <q-tab class="bg-green-2" :name="'new'" icon="schema"
               label="Create New Flow"/>
        <q-tab v-for="flow in flows"
               :name="flow.id"
               :icon="flow.activate?'start':'stop'"
               :class="flow.activate?'bg-green-1':'bg-red-1'"
               :label="flow.name?flow.name:flow.id">
        </q-tab>
      </q-tabs>
    </template>
    <template v-slot:after>
      <q-tab-panels
          vertical
          v-model="tab">
        <q-tab-panel :name="'new'" icon="schema">
          <FlowEditor :onChangeFlow="onChangeFlow" :flow="null"/>
        </q-tab-panel>
        <q-tab-panel v-for="flow in flows" :name="flow.id" icon="schema">
          <FlowEditor :onChangeFlow="onChangeFlow" :flow="flow"/>
        </q-tab-panel>
      </q-tab-panels>
    </template>
  </q-splitter>
</template>

<script setup lang="ts">

import {flowApi} from "~/composables/api/FlowApi";
import {onMounted, ref} from 'vue'
import FlowEditor from "~/components/flows/FlowEditor.vue";
import type {FlowT} from "~/composables/api/StructureApp";

definePageMeta({
  layout: "panel",
  middleware: "page-any-roles"
})

const runtimeConfig = useRuntimeConfig();
const {getAll, deleteFlow} = flowApi(runtimeConfig)
const {data: flows, refresh} = useAsyncData('flows', getAll);

const tab = ref('')
const width = ref(200)

const onChangeFlow = () => {
  onMounted()
  refresh();
}

onMounted(() => {
  setTimeout(() => {
    if (flows && flows.value && flows.value?.length >= 1) {
      tab.value = flows.value[0].id;
    }
  }, 100)
})


</script>

