<script lang="ts" setup>
import {onMounted, ref, watch} from 'vue'
// import {useVueFlow} from "@vue-flow/core";

const commends = ref("");
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

  commends.value = props.sensor.commends
})
// const {updateNode} = useVueFlow()

watch(commends, () => {
  handleUpdate();
})

function handleUpdate() {
  // updateNode(props.id, {
  //   sensor: {
  //     commends: commends
  //   }
  // } as any)
}
</script>

<template>
  <el-input v-model="commends"  type="textarea" label="Commends" hint="Open windows; Is to hot"/>
</template>
