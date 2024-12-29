<script lang="ts" setup>
import {onMounted, ref, watch} from 'vue'
import Node from "~/components/flows/nodes/Node.vue";
// import {useVueFlow} from "@vue-flow/core";

const cron = ref("*/30 * * * *");
const props = defineProps({
  id: {
    type: String,
    required: true,
  },
  sensor: {
    type: Object as () => any,
    required: false,
  } as any,
})

onMounted(() => {
  if (!props.sensor) {
    return;
  }
  cron.value = props.sensor.cron
})

// const {updateNode} = useVueFlow()

watch(cron, () => {
  handleUpdate();
})

function handleUpdate() {
  // updateNode(props.id, {
  //   sensor: {
  //     cron: cron
  //   }
  // } as any)
}
</script>

<template>
  <Node name="DSasdsadsa">

    <template #visable>
      <el-input label="Time execute" hint="Cron format e.g. */30 * * * *  or 100 in millisecond" @keyup="handleUpdate" v-model="cron"></el-input>
      <el-input label="Time execute" hint="Cron format e.g. */30 * * * *  or 100 in millisecond" @keyup="handleUpdate" v-model="cron"></el-input>
      <el-input label="Time execute" hint="Cron format e.g. */30 * * * *  or 100 in millisecond" @keyup="handleUpdate" v-model="cron"></el-input>
    </template>
  </Node>
</template>
