<script lang="ts" setup>
import {ref, watch} from 'vue'
import {useVueFlow} from "@vue-flow/core";

const code = ref("let result=true");
const props = defineProps({
  id: {
    type: String,
    required: true,
  },
})
const {updateNode} = useVueFlow()

watch(code, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {"sensor": code})
}
</script>

<template>
  <div><strong>{{ props.id }}</strong></div>
  <q-input type="textarea" label="Code JS"  @keyup="handleUpdate" v-model="code"></q-input>
</template>
