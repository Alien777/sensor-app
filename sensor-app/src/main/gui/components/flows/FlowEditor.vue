<script setup lang="ts">
import {defineProps, onMounted, ref} from 'vue'
import {type Connection, type Edge, MarkerType, useVueFlow, VueFlow} from '@vue-flow/core'
import {
  type ConfigNode,
  draggableItems,
  type FlowT,
  type Node,
  type NodeDraggable
} from "~/composables/api/StructureApp";
import DropzoneBackground from "~/components/flows/DropzoneBackground.vue";
import useDragAndDrop from "~/composables/VueFlowUtils";
import {MiniMap} from "@vue-flow/minimap";
import {flowApi} from "~/composables/api/FlowApi";
import {Notify, useQuasar} from "quasar";
import EdgeWithButton from "~/components/flows/EdgeWithButton.vue";


definePageMeta({
  layout: "panel",
  middleware: "page-any-roles"
})


const {onDragStart} = useDragAndDrop();

const {onDragOver, onDrop, onDragLeave, isDragOver, insertNode, insertEdge} = useDragAndDrop()
const innerTab = ref('')
const splitterModel = ref(200)
const {onConnect, getEdges, removeEdges, addEdges, toObject, setViewport} = useVueFlow()
const runtimeConfig = useRuntimeConfig();
const {saveFlow, getAll, deleteFlow, get, startFlow, stopFlow} = flowApi(runtimeConfig)


const props = defineProps({
  flow: {
    type: Object as () => FlowT,
    required: true
  },
  onChangeFlow: {
    type: Function as PropType<() => void>,
    required: true
  }
});

// Tworzenie lokalnej zmiennej reaktywnej
const name = ref(props.flow?.name);
const config = ref(props.flow?.config);

const nodes = ref<any[]>([]);
const edges = ref<any[]>([]);
const nameRules = [
  val => (val && val.length > 0) || 'Field is required',
  val => (val && val.length >= 2) || 'Name must be at least 1 characters',
  val => (val && val.length <= 40) || 'Name must be less than 40 characters'
];
const convert = (node: ConfigNode) => {

  node.nodes.forEach((node: Node) => {
    const n = {
      id: node.ref,
      name: node.name,
      type: node.type,
      position: node.position,
      sensor: node.sensor,
    }

    insertNode(n, node.ref, node.position);
    let edgeId = 1;
    node.childed.forEach(childRef => {
      const x = (): Edge => {
        edgeId++;

        return {
          id: node.ref + '_' + childRef + '_ID_' + edgeId,
          source: node.ref,
          target: childRef
        };
      };
      insertEdge(x());
    });
  });
  setViewport(node.viewport);
}

onMounted(() => {
  setTimeout(() => {
    if (flows && flows.value && flows.value?.length >= 1) {
      innerTab.value = String(flows.value[0].id);
      name.value = props.flow?.name;
      config.value = props.flow?.config;
      nodes.value = [];
      edges.value = []
      let parse = JSON.parse(config.value) as ConfigNode;
      convert(parse);
    }

  }, 100)
})
const handleConnect = (params: Connection) => {
  const {source, sourceHandle, target, targetHandle} = params;
  const edgeExists = getEdges.value.some(
      (edge) =>
          edge.source == source &&
          edge.target == target
  );
};

const {data: flows} = useAsyncData('flows', getAll);
const onSave = () => {

  if (name.value == null || name.value.length <= 1) {
    Notify.create({
      type: 'negative',
      message: 'Please fill the Name field. min 1 characters'
    })
    return;
  }
  const nodes = toObject();

  const nod = nodes.nodes.map((value: any) => {
    return {
      ref: value.id,
      name: value.name,
      childed: [],
      position: value.position,
      type: value.type,
      sensor: value.sensor
    };
  })
  nod.forEach(myNod => {
    myNod.childed = nodes.edges.filter(n => n.source === myNod.ref).map(value => value.target);
  })
  const config = {
    nodes: nod,
    viewport: nodes.viewport
  }
  const x = {
    id: props.flow?.id,
    name: name,
    config: JSON.stringify(config)
  }
  saveFlow(x).finally(() => {
    props.onChangeFlow()
  })
}

onConnect(handleConnect)
const $q = useQuasar();
</script>
<template>
  <div v-if="!flow || flow.id!==null" class="text-h5"><p>Editing: {{ name }}</p></div>
  <hr>
  <div class="q-pb-md">
    <q-input maxlength="40"
             label="Name"
             :rules="nameRules"
             v-model="name">
    </q-input>
    <q-btn-group  spread>
      <q-btn no-caps icon="save" class="bg-green-1" @click="onSave">{{
          flow && flow.id ? 'Save flow' : 'Save new flow'
        }}
      </q-btn>
      <q-btn no-caps @click="()=>
        startFlow(flow.id).finally(() => props.onChangeFlow())
 " v-if="flow && !flow.isActivate" icon="start" class="bg-green-3">Start
      </q-btn>
      <q-btn no-caps @click="()=> stopFlow(flow.id).finally(() => props.onChangeFlow())"
             v-else-if="flow && flow.isActivate"
             icon="stop" class="bg-red-1">Stop
      </q-btn>

      <q-btn  v-if="flow" @click="()=>  {

       $q.dialog({
        title: 'Confirm',
        message: `Would you like to turn delete flow ${name}`,
        cancel: true,
        dark: true,
        color: 'red',
      }).onOk(() => {
       deleteFlow(flow.id).finally(() => props.onChangeFlow())
      });
      }"
             no-caps icon="delete" class="bg-red-4">Delete
      </q-btn>
    </q-btn-group>

  </div>
  <q-splitter v-model="splitterModel" style=" height: calc(100vh - 280px);" unit="px" class="dndflow" @drop="onDrop">
    <template v-slot:before>
      <div style="overflow-y: auto">
        <div style="max-height: calc(100vh - 280px);">
          <div v-for="item in draggableItems.filter(v => v.type==='input')" :key="item.name"
               :class="`vue-flow__node-${item.type}`"
               :draggable="true"
               @dragstart="onDragStart($event, {type: item.type, name: item.name} as NodeDraggable)">
            {{ item.readableName }}
          </div>
          <hr>
          <div v-for="item in draggableItems.filter(v => v.type==='default')" :key="item.name"
               :class="`vue-flow__node-${item.type}`"
               :draggable="true"
               @dragstart="onDragStart($event, {type: item.type,name: item.name} as NodeDraggable)">
            {{ item.readableName }}
          </div>
        </div>
      </div>

    </template>
    <template v-slot:after>
      <VueFlow :connection-radius="30" auto-connect :nodes="nodes" :edges="edges" delete-key-code=false
               :default-edge-options="{
      type: 'button',
      style: { stroke: 'black' },
      markerEnd: MarkerType.ArrowClosed,
      animated: true
    }" style="width: 100%; height: 100%" @dragover="onDragOver" @dragleave="onDragLeave">
        <template #edge-button="buttonEdgeProps">
          <EdgeWithButton
              :id="buttonEdgeProps.id"
              :source-x="buttonEdgeProps.sourceX"
              :source-y="buttonEdgeProps.sourceY"
              :target-x="buttonEdgeProps.targetX"
              :target-y="buttonEdgeProps.targetY"
              :source-position="buttonEdgeProps.sourcePosition"
              :target-position="buttonEdgeProps.targetPosition"
              :marker-end="buttonEdgeProps.markerEnd"
              :style="buttonEdgeProps.style"
          />
        </template>
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
