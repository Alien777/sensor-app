<script lang="ts" setup>
import {onMounted, ref, watch} from 'vue'
import {useVueFlow} from "@vue-flow/core";

const sleepTimeSeconds = ref("5");
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
  sleepTimeSeconds.value = props.sensor.sleepTimeSeconds
})
const {updateNode} = useVueFlow()

watch(sleepTimeSeconds, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {
    sensor: {
      sleepTimeSeconds: sleepTimeSeconds
    }
  })
}
</script>

<template>
  <div><strong>{{ props.id }}</strong></div>
  <q-input label="Sleep time [s]" @keyup="handleUpdate" v-model="sleepTimeSeconds"></q-input>
</template>
