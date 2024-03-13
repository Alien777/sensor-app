<script setup lang="ts">
const props = defineProps({
  id: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <div><strong>{{ props.id }}</strong></div>
</template>

<style scoped lang="css">

</style>