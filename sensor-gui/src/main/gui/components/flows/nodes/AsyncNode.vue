<script setup lang="ts">
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
</script>

<template>
  <div><strong>{{ props.id }}</strong></div>
</template>

<style scoped lang="css">

</style>