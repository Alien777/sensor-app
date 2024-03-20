<script lang="ts" setup>
import {onMounted, ref, watch} from 'vue'
import {useVueFlow} from "@vue-flow/core";

const code = ref("let result=true");
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

  code.value = props.sensor.code
})
const {updateNode} = useVueFlow()

watch(code, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {
    sensor: {
      code: code
    }
  })
}
</script>

<template>
  <q-input type="textarea" label="Code JS" @keyup="handleUpdate" v-model="code"></q-input>
</template>
