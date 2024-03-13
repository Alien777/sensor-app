<script lang="ts" setup>
import {ref, watch} from 'vue'
import {useVueFlow} from "@vue-flow/core";

const cron = ref("*/30 * * * *");
const props = defineProps({
  id: {
    type: String,
    required: true,
  },
})
const {updateNode} = useVueFlow()

watch(cron, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {"sensor": cron})
}
</script>

<template>
  <div><strong>{{ props.id }}</strong></div>
  <q-input label="Time execute" hint="Cron format e.g. */30 * * * *" @keyup="handleUpdate" v-model="cron"></q-input>
</template>
