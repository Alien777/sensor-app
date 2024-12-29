<script lang="ts" setup>


const sleepTime = ref(5000);
const props = defineProps({
  id: {
    type: String,
    required: true,
  },
  sensor: {
    type: Object as () => any,
    required: false,
  },
  callback: {
    type: Function,
    required: true
  }
})

onMounted(() => {
  if (!props.sensor) {
    return;
  }
  sleepTime.value = props.sensor.sleepTime
})

watch(sleepTime, () => {
  handleUpdate();
})

function handleUpdate() {
  props.callback(props.id, {
    sensor: {
      sleepTime: sleepTime.value
    }
  } as any)
}
</script>

<template>
  <el-input placeholder="Sleep time [millisecond]" v-model="sleepTime"></el-input>
</template>
