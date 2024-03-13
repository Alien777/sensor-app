<script setup>
import {ref, reactive} from 'vue'
import {MarkerType, useVueFlow, VueFlow} from '@vue-flow/core'


import DropzoneBackground from "~/components/flows/DropzoneBackground.vue";
import Sidebar from "~/components/flows/Sidebar.vue";
import useDragAndDrop from "~/composables/useDnD";
import {MiniMap} from "@vue-flow/minimap";


definePageMeta({
  layout: "panel",
  middleware: "page-any-roles"
})
const nodes = ref([])
const edges = ref([])
const {onConnect, addEdges, toObject} = useVueFlow()

const {onDragOver, onDrop, onDragLeave, isDragOver} = useDragAndDrop()


const handleConnect = (params) => {
  const {sourceHandle, targetHandle} = params;
  if (sourceHandle.endsWith('__handle-bottom') && targetHandle.endsWith('__handle-top')) {
    addEdges([params]);
  }
};
const onSave = () => {
  console.log(toObject())
}

onConnect(handleConnect)
</script>

<template>
  <div class="dndflow" @drop="onDrop">
    <Panel position="top-right" class="save-restore-controls">
      <button style="background-color: #33a6b8" @click="onSave">save</button>
    </Panel>
    <VueFlow :nodes="nodes" :edges="edges" :default-edge-options="{
      type: 'step',
      style: { stroke: 'orange' },
      markerEnd: MarkerType.ArrowClosed,
      animated: true
    }" style="width: 700px; height: 700px" @dragover="onDragOver" @dragleave="onDragLeave">

      <MiniMap/>
      <DropzoneBackground
          :style="{
          backgroundColor: isDragOver ? '#e7f3ff' : 'transparent',
          transition: 'background-color 0.1s ease',
        }"
      >
      </DropzoneBackground>
    </VueFlow>

    <Sidebar/>
  </div>

</template>
<style lang="css" scoped>
.dndflow {
  flex-direction: column;
  display: flex;
  height: 100%
}

.dndflow aside {
  color: #fff;
  font-weight: 700;
  border-right: 1px solid #eee;
  padding: 15px 10px;
  font-size: 12px;
  background: rgba(16, 185, 129, .75);
  -webkit-box-shadow: 0px 5px 10px 0px rgba(0, 0, 0, .3);
  box-shadow: 0 5px 10px #0000004d
}

.dndflow aside .nodes > * {
  margin-bottom: 10px;
  cursor: grab;
  font-weight: 500;
  -webkit-box-shadow: 5px 5px 10px 2px rgba(0, 0, 0, .25);
  box-shadow: 5px 5px 10px 2px #00000040
}

.dndflow aside .description {
  margin-bottom: 10px
}

.dndflow .vue-flow-wrapper {
  flex-grow: 1;
  height: 100%
}

@media screen and (min-width: 640px) {
  .dndflow {
    flex-direction: row
  }

  .dndflow aside {
    min-width: 25%
  }
}

@media screen and (max-width: 639px) {
  .dndflow aside .nodes {
    display: flex;
    flex-direction: row;
    gap: 5px
  }
}

.save-restore-controls {
  font-size: 12px
}

.save-restore-controls button {
  margin-left: 5px;
  padding: 5px;
  border-radius: 5px;
  font-weight: 700;
  text-transform: uppercase;
  color: #fff;
  -webkit-box-shadow: 0px 5px 10px 0px rgba(0, 0, 0, .3);
  box-shadow: 0 5px 10px #0000004d;
  cursor: pointer
}

.save-restore-controls button:hover {
  transform: scale(105%);
  transition: .25s all ease-in-out
}
</style>
