<script lang="ts" setup>
import {ref, watch} from 'vue'
import {useVueFlow} from "@vue-flow/core";

const sleepTimeSeconds = ref("5");
const props = defineProps({
  id: {
    type: String,
    required: true,
  },
})
const {updateNode} = useVueFlow()

watch(sleepTimeSeconds, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {"sensor": sleepTimeSeconds})
}
</script>

<template>
  <div><strong>{{ props.id }}</strong></div>
  <q-input label="Sleep time [s]" @keyup="handleUpdate" v-model="sleepTimeSeconds"></q-input>
</template>
