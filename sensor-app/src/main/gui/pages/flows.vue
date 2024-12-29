<template>
  <div style="display: flex;">
    <ListOfNode/>

    <div style="padding-bottom: 20px; width: 100%">
      <div style="display: flex; gap: 10px;">
        <el-button size="large" @click="addNew()">
          Create New Flow
        </el-button>

        <el-button size="large" @click="save()">
          Save
        </el-button>
        <el-input v-model="filter" placeholder="Filter" @input="applyFilter"></el-input>
      </div>
      <br>
      <el-tabs
          v-model="editableTabsValue"
          type="card"
          class="demo-tabs"
          closable
          @tab-remove="removeTab"
      >
        <el-tab-pane
            v-for="item in originalTabs"
            :key="item.name"
            :label="item.title"
            :name="item.name"
        >
          <FlowsFlowEditor/>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue';
import ListOfNode from "~/components/flows/ListOfNode.vue";
import {flowApi} from "~/composables/api/FlowApi";
import type {FlowT} from "~/composables/api/StructureApp";
const runtimeConfig = useRuntimeConfig();
let flow = flowApi(runtimeConfig);
let tabIndex = 1;
const editableTabsValue = ref('2');
const filter = ref('');
const originalTabs = ref([
  {
    title: 'Tab 1',
    name: '1',
  },
  {
    title: 'Tab 2',
    name: '2',
  },
]);


const applyFilter = () => {
  const filteredTabs = originalTabs.value.filter((tab) =>
      tab.title.toLowerCase().includes(filter.value.toLowerCase())
  );

  if (filteredTabs.length > 0) {
    editableTabsValue.value = filteredTabs[0].name;
  } else {
    editableTabsValue.value = '';
  }
};

const save = () => {
  // const f=<FlowT>{
  //   name:
  // }
  // flow.saveFlow()
}

const addNew = () => {
  const newTabName = `${++tabIndex}`;
  const newTab = {
    title: 'New Tab',
    name: newTabName,
  };

  originalTabs.value.push(newTab);
  editableTabsValue.value = newTabName;
};

const removeTab = (targetName: string) => {
  const index = originalTabs.value.findIndex((tab) => tab.name === targetName);
  if (index > -1) originalTabs.value.splice(index, 1);
  if (editableTabsValue.value === targetName) {
    const nextTab = originalTabs.value[index] || originalTabs.value[index - 1];
    editableTabsValue.value = nextTab ? nextTab.name : '';
  }
};
</script>

<style>

.demo-tabs > .el-tabs__content {
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
  width: 100%;
}
</style>
