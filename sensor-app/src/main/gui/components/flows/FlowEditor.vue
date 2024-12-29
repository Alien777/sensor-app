<template>
  <el-button @click="save">Save</el-button>
  <div class="drawflow-container">
    <div ref="drawflow" class="drawflow"></div>
  </div>
</template>

<script setup lang="ts">
import 'drawflow/dist/drawflow.min.css';
import '../../public/drawflow.css';
import {getCurrentInstance, h, render} from 'vue';
import {draggableItems} from "~/composables/api/StructureApp";
import {flowApi} from "~/composables/api/FlowApi";

const runtimeConfig = useRuntimeConfig();
let flow = flowApi(runtimeConfig);
const drawflow = ref(null);
const Vue = {version: 3, h, render};
const internalInstance = getCurrentInstance();
let editor = null;

const save = () => {
  console.log(editor)
}

const drop = (event) => {
  if (!editor) {
    console.error('Editor not initialized');
    return;
  }
  const nodeType = event.dataTransfer.getData('nodeType');
  if (!editor.noderegister[nodeType]) {
    return;
  }

  const position = drawflow.value.getBoundingClientRect();
  const x = event.clientX - position.left;
  const y = event.clientY - position.top;

  editor.addNode(nodeType, 1, 1, x, y, nodeType, {}, nodeType, 'vue');
}

onMounted(() => {
  const container = drawflow.value;

  import('drawflow').then(({default: Drawflow}) => {
    editor = new Drawflow(container, Vue, internalInstance.appContext.app._context);
    editor.reroute = true
    editor.start();

    function updateData(id, data) {
      console.log('Callback triggered with data:', id, data);
    }

    for (let node of draggableItems) {

      editor.registerNode(node.name, node.component, {callback: updateData}, {});
    }

    container.addEventListener('dragover', (event) => {
      event.preventDefault();
    });

    container.addEventListener('drop', (event) => {
      event.preventDefault();
      drop(event);
    });

    editor.on('nodeChanged', (id) => {
      console.log('Node changed:', id, editor.export());
    });

    editor.on('connectionCreated', (connection) => {
      console.log('Connection created:', connection, editor.export());
    });
  });
});
</script>

<style scoped>

.drawflow-container {
  width: 100%;
  height: calc(100vh - 300px);
  display: flex;
  justify-content: center;
  align-items: center;

}

.drawflow {
  width: 100%;
  height: 100%;
  border: 1px solid #ddd;
  background: #f7f7f7;
}


</style>
