<template>
  <el-container style="height: 100vh;">
    <el-aside :width="`${width}px`">
      <!-- Tabs -->
      <el-tabs
          v-model="tab"
          tab-position="left"
          class="custom-tabs"
      >
        <el-tab-pane name="new" label="Create New Flow">
          <template #label>
            <i class="el-icon-s-opportunity"></i> Create New Flow
          </template>
        </el-tab-pane>
        <el-tab-pane
            v-for="flow in flows"
            :key="flow.id"
            :name="flow.id"
            :label="flow.name || flow.id"
        >
          <template #label>
            <i :class="flow.isActivate ? 'el-icon-play' : 'el-icon-stop'"></i>
            {{ flow.name || flow.id }}
          </template>
        </el-tab-pane>
      </el-tabs>
    </el-aside>
    <el-main>
      <!-- Tab Panels -->
      <div v-if="tab === 'new'">
        <FlowEditor :onChangeFlow="onChangeFlow" :flow="null" />
      </div>
      <div
          v-for="flow in flows"
          :key="flow.id"
          v-show="tab === flow.id"
      >
        <FlowEditor :onChangeFlow="onChangeFlow" :flow="flow" />
      </div>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import {flowApi} from "~/composables/api/FlowApi";
import FlowEditor from "~/components/flows/FlowEditor.vue";

definePageMeta({
  middleware: "page-any-roles",
});

const runtimeConfig = useRuntimeConfig();
const { getAll } = flowApi(runtimeConfig);
const { data: flows, refresh } = useAsyncData("flows", getAll);

const tab = ref("new");
const width = ref(200);

const onChangeFlow = () => {
  onMounted(() => {
    refresh();
  });
};

onMounted(() => {
  setTimeout(() => {
    if (flows && flows.value && flows.value.length >= 1) {
      tab.value = flows.value[0].id;
    }
  }, 100);
});
</script>

<style scoped>
.custom-tabs {
  height: 100%;
  overflow-y: auto;
}
</style>
