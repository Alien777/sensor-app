<script lang="ts" setup>
import {deviceApi} from "~/composables/api/DeviceApi";
import SelectLazy from "~/components/common/SelectLazy.vue";
import {watch} from "vue";
import {useVueFlow} from "@vue-flow/core";

const {updateNode} = useVueFlow()

const props = defineProps({
  id: {
    type: String,
    required: true,
  },
})
const provideDataDevice = (value: any) => {
  getAllDevice().then(v => {
    value(v.map(a => a.id));
  })
}

const runtimeConfig = useRuntimeConfig();
const {getAllDevice} = deviceApi(runtimeConfig);
const deviceId = ref(null);


watch(deviceId, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {
    sensor: {
      deviceId: deviceId
    }
  })
}
</script>

<template>
  <strong>{{ props.id }}</strong>
  <SelectLazy v-model="deviceId" :provide-data="provideDataDevice" label="Device Id"/>
  <SelectLazy v-model="deviceId" :provide-data="provideDataDevice" label="Device Id"/>

</template>
