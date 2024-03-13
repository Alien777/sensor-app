<script lang="ts" setup>


import useDragAndDrop from "~/composables/useDnD";

const {onDragStart} = useDragAndDrop()
</script>

<template>
  <aside>
    <div class="description">You can drag these nodes to the pane.</div>

    <div class="nodes">
      <hr>Start
      <div class="vue-flow__node-input" :draggable="true" @dragstart="onDragStart($event, {
        type: 'input',
        name: 'ListeningSensorNode'
      })">ListeningSensorNode
      </div>
      <div class="vue-flow__node-input" :draggable="true" @dragstart="onDragStart($event, {
        type: 'input',
        name: 'CronNode'
      })">CronNode</div>
      <hr>Others
      <div class="vue-flow__node-input" :draggable="true" @dragstart="onDragStart($event, {
        type: 'default',
        name: 'SendPwmValueNode'
      })">SendPwmValueNode
      </div>
      <div class="vue-flow__node-default" :draggable="true" @dragstart="onDragStart($event, {
        type: 'default',
        name: 'ExecuteCodeNode'
      })">
        ExecuteCodeNode
      </div>
      <hr>Threads
      <div class="vue-flow__node-default" :draggable="true" @dragstart="onDragStart($event, {
        type: 'default',
        name: 'AsyncNode'
      })">AsyncNode</div>
      <div class="vue-flow__node-default" :draggable="true" @dragstart="onDragStart($event, {
        type: 'default',
        name: 'SleepNode'
      })">SleepNode</div>
      <div class="vue-flow__node-default" :draggable="true" @dragstart="onDragStart($event, {
        type: 'default',
        name: 'WaitNode'
      })">WaitNode</div>
    </div>
  </aside>
</template>
