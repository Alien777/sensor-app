<template>
  <el-container style="height: 100vh;">
    <el-aside :width="`${splitterModel}px`">
      <!-- Tabs -->
      <el-tabs
          v-model="innerTab"
          tab-position="left"
          class="custom-tabs"
      >
        <!-- Add New Device Tab -->
        <el-tab-pane name="newDevice" label="Add New Device">
          <template #label>
            <i class="el-icon-plus"></i>
            Add New Device
          </template>
        </el-tab-pane>
        <!-- Device Tabs -->
        <el-tab-pane
            v-for="device in devices"
            :key="device.id"
            :name="device.id"
        >
          <template #label>
            <i :class="device.version ? 'el-icon-monitor' : 'el-icon-loading'"></i>
            <div>
              <p>{{ device.name.substring(0, 20) }}</p>
              <p v-if="device.version" class="device-id">{{ device.id }}</p>
            </div>
          </template>
        </el-tab-pane>
      </el-tabs>
    </el-aside>
    <el-main>
      <!-- Panels -->
      <div v-if="innerTab === 'newDevice'">
        <div class="text-h5">
          <p>Create New Device</p>
        </div>
        <hr />
        <NewDevice :onChange="onChange" />
      </div>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { deviceApi } from "~/composables/api/DeviceApi";

const innerTab = ref("newDevice");
const splitterModel = ref(200);

const runtimeConfig = useRuntimeConfig();
const { getAllDevice } = deviceApi(runtimeConfig);
const { data: devices, refresh } = useAsyncData("devices", () =>
    getAllDevice(true)
);

const onChange = () => {
  refresh();
};

onMounted(() => {
  setTimeout(() => {
    if (devices && devices.value && devices.value.length >= 1) {
      innerTab.value = devices.value[0].id;
    } else {
      innerTab.value = "newDevice";
    }
  }, 100);
});

definePageMeta({
  middleware: "page-any-roles",
});
</script>

<style scoped>
.custom-tabs {
  height: 100%;
  overflow-y: auto;
}
.device-id {
  background-color: #e6f7ff;
  padding: 4px;
  border-radius: 4px;
  font-size: 12px;
}
</style>
