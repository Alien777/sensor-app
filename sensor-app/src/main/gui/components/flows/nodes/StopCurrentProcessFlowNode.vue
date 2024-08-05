<script lang="ts" setup>
import {onMounted, ref, watch} from 'vue'
import {useVueFlow} from "@vue-flow/core";
import SelectLazy from "~/components/common/SelectLazy.vue";
import {flowApi} from "~/composables/api/FlowApi";
const runtimeConfig = useRuntimeConfig();
const { getAll} = flowApi(runtimeConfig)
const flowId = ref("");
const props = defineProps({
  id: {
    type: String,
    required: true,
  },
  sensor: {
    type: Object as () => any,
    required: false,
  },
})

onMounted(() => {
  if (!props.sensor) {
    return;
  }
  flowId.value = props.sensor.flowId
})
const {updateNode} = useVueFlow()

watch(flowId, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {
    sensor: {
      flowId: flowId
    }
  } as any)
}

const provideDataFlows = (value: any) => {
  getAll().then(v => {
    value(v.map(a => ({
      "value": a.id,
      "label": a.name,
    })));
  })
}
</script>

<template>
  <SelectLazy v-model="flowId" :provide-data="provideDataFlows" label="Flow to restart"/>
</template>
