<template>
  <div class="menu">
    <el-input size="large" v-model="filter" placeholder="Filter" @input="applyFilter"></el-input>
    <br>
    <br>
    <div v-for="node in filteredItems"
         :key="node.name"
         class="drag-item"
         draggable="true"
         @dragstart="(e) => drag(e, node.name)"
    >
      {{ node.name }}
    </div>
  </div>
</template>


<script setup>
import 'drawflow/dist/drawflow.min.css';
import '../../public/drawflow.css';
import {computed, ref} from 'vue';
import {draggableItems} from "~/composables/api/StructureApp";

// Filtr (wartość wpisana w pole)
const filter = ref('');

// Filtrowane elementy
const filteredItems = computed(() =>
    draggableItems.filter((node) =>
        node.name.toLowerCase().includes(filter.value.toLowerCase())
    )
);

function drag(event, nodeType) {
  event.dataTransfer.setData('nodeType', nodeType);
}

</script>

<style scoped>

.menu {
  top: 10px;
  left: 10px;
  padding-right: 10px;
  flex-direction: column;
  gap: 10px;
  min-width: 300px;
}

.drag-item {
  padding: 10px;
  background-color: #ddd;
  border: 1px solid #bbb;
  cursor: grab;
}

.drag-item:active {
  cursor: grabbing;
}
</style>
