<script lang="ts" setup>
import {onMounted, ref, watch} from 'vue'
import {useVueFlow} from "@vue-flow/core";

const sleepTime = ref("5000");
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
  sleepTime.value = props.sensor.sleepTime
})
const {updateNode} = useVueFlow()

watch(sleepTime, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {
    sensor: {
      sleepTime: sleepTime
    }
  } as any)
}
</script>

<template>
  <q-input label="Sleep time [millisecond]" @keyup="handleUpdate" v-model="sleepTime"></q-input>
</template>
