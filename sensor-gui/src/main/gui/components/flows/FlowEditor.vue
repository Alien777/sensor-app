<script setup lang="ts">
import {defineProps, onMounted, ref} from 'vue'
import {MarkerType, useVueFlow, VueFlow} from '@vue-flow/core'
import {type FlowT} from "~/composables/api/StructureApp";
import DropzoneBackground from "~/components/flows/DropzoneBackground.vue";

import useDragAndDrop from "~/composables/useDnD";
import {MiniMap} from "@vue-flow/minimap";
import {flowApi} from "~/composables/api/FlowApi";

const props = defineProps({
  flow: {
    type: Object as () => FlowT,
    required: true
  }
});
definePageMeta({
  layout: "panel",
  middleware: "page-any-roles"
})
const nodes = ref([])
const edges = ref([])
const {onConnect, addEdges, toObject} = useVueFlow()
const runtimeConfig = useRuntimeConfig();
const {saveFlow, getAll} = flowApi(runtimeConfig)


const {onDragStart} = useDragAndDrop();
const draggableItems = ref([
  {type: 'input', name: 'ListeningSensorNode'},
  {type: 'input', name: 'CronNode'},
  {type: 'default', name: 'SendPwmValueNode'},
  {type: 'default', name: 'ExecuteCodeNode'},
  {type: 'default', name: 'AsyncNode'},
  {type: 'default', name: 'SleepNode'}
]);
const {onDragOver, onDrop, onDragLeave, isDragOver} = useDragAndDrop()
const val = 200
const innerTab = ref('')
const splitterModel = ref(200)


onMounted(() => {
  setTimeout(() => {
    if (flows && flows.value && flows.value?.length >= 1) {
      innerTab.value = flows.value[0].id;
    }

  }, 100)
})
const handleConnect = (params) => {
  const {sourceHandle, targetHandle} = params;
  if (sourceHandle.endsWith('__handle-bottom') && targetHandle.endsWith('__handle-top')) {
    addEdges([params]);
  }
};
const {data: flows} = useAsyncData('flows', getAll);
const onSave = () => {
  const nodes = toObject();
  console.log(nodes)

  const nod = nodes.nodes.map(value => {
    return {
      ref: value.id,
      name: value.id,
      childed: [],
      position: value.position,
      sensor: value.sensor
    };
  })
  nod.forEach(myNod => {
    myNod.childed = nodes.edges.filter(n => n.source === myNod.ref).map(value => value.target);
  })
  console.log(nod)
  console.log(props.flow?.name)
  // saveFlow(nod)
}

onConnect(handleConnect)
</script>

<template>
  <div class="q-pb-md">
    <q-input label="Name" :model-value="props.flow?.name"></q-input>
    <q-btn-group spread>
      <q-btn icon="save" color="green" @click="onSave">{{ flow && flow.id ? 'Save flow' : 'Save new flow'}}</q-btn>
      <q-btn v-if="flow && !flow.isActivate" icon="start" text-color="black" color="yellow">Start</q-btn>
      <q-btn v-else-if="flow && flow.isActivate" icon="stop" color="red">Stop</q-btn>
    </q-btn-group>
  </div>
  <q-splitter v-model="splitterModel" style=" height: calc(100vh - 150px);" unit="px" class="dndflow" @drop="onDrop">
    <template v-slot:before>
      <q-card>
        <q-card-section>
          <div class="text-h6">Start nodes</div>
        </q-card-section>
        <q-card-section>
          <div v-for="item in draggableItems.filter(v => v.type==='input')" :key="item.name"
               :class="`vue-flow__node-${item.type}`"
               :draggable="true" @dragstart="onDragStart($event, {type: item.type,name: item.name})">
            {{ item.name }}
          </div>
        </q-card-section>
      </q-card>
      <q-card>
        <q-card-section>
          <div class="text-h6">Nodes</div>
        </q-card-section>
        <q-card-section>
          <div v-for="item in draggableItems.filter(v => v.type==='default')" :key="item.name"
               :class="`vue-flow__node-${item.type}`"
               :draggable="true" @dragstart="onDragStart($event, {type: item.type,name: item.name})">
            {{ item.name }}
          </div>
        </q-card-section>
      </q-card>
    </template>
    <template v-slot:after>
      <VueFlow :nodes="nodes" :edges="edges" :default-edge-options="{
      type: 'step',
      style: { stroke: 'black' },
      markerEnd: MarkerType.ArrowClosed,
      animated: true
    }" style="width: 100%; height: 100%" @dragover="onDragOver" @dragleave="onDragLeave">
        <MiniMap/>
        <DropzoneBackground
            :style="{
          backgroundColor: isDragOver ? '#e7f3ff' : 'transparent',
          transition: 'background-color 0.1s ease',
        }">
        </DropzoneBackground>
      </VueFlow>
    </template>
  </q-splitter>

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
  background: var(--q-info) !important;
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
