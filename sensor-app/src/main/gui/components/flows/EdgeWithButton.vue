<script setup>
import {BaseEdge, EdgeLabelRenderer, getBezierPath, useVueFlow} from '@vue-flow/core'
import {computed} from 'vue'

const props = defineProps({
  id: {
    type: String,
    required: true,
  },
  sourceX: {
    type: Number,
    required: true,
  },
  sourceY: {
    type: Number,
    required: true,
  },
  targetX: {
    type: Number,
    required: true,
  },
  targetY: {
    type: Number,
    required: true,
  },
  sourcePosition: {
    type: String,
    required: true,
  },
  targetPosition: {
    type: String,
    required: true,
  },
  markerEnd: {
    type: String,
    required: false,
  },
  style: {
    type: Object,
    required: false,
  },
})

const {removeEdges} = useVueFlow()

const path = computed(() => getBezierPath(props))
</script>

<script>
export default {
  inheritAttrs: false,
}
</script>
<template>
  <BaseEdge :id="id" :style="style" :path="path[0]" :marker-end="markerEnd"/>
  <EdgeLabelRenderer>
    <div
        :style="{
        pointerEvents: 'all',
        position: 'absolute',
        transform: `translate(-50%, -50%) translate(${path[1]}px,${path[2]}px)`,
      }"
        class="nodrag nopan"
    >
      <q-btn @click="removeEdges(id)" size="xs" icon="delete" color="red"/>
    </div>
  </EdgeLabelRenderer>
</template>
