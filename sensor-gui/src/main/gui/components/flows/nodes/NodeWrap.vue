<script setup lang="ts">
import {defineProps} from 'vue'

const props = defineProps<{
  name: string
  id: string
}>()
</script>

<template>
  <component :is="props.name" :id="props.id"/>
</template>