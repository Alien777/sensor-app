<script lang="ts" setup>
import {deviceApi} from "~/composables/api/DeviceApi";
import SelectLazy from "~/components/common/SelectLazy.vue";
import {watch} from "vue";
import {useVueFlow} from "@vue-flow/core";
import {configUtilsApi} from "~/composables/api/ConfigUtilsApi";

const {updateNode} = useVueFlow()
const runtimeConfig = useRuntimeConfig();
const {getAllDevice} = deviceApi(runtimeConfig);
const {getMessageTypes} = configUtilsApi(runtimeConfig);
const deviceId = ref(null);
const messageType = ref(null);


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

const provideDataMessageType = (value: any) => {
  if (deviceId.value == null) {
    return;
  }
  getMessageTypes(deviceId.value).then(v => {
    value(v);
  })
}
watch(messageType, () => {
  handleUpdate();
})

watch(deviceId, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {
    sensor: {
      deviceId: deviceId,
      messageType: messageType
    }
  })
}
</script>

<template>
  <strong>{{ props.id }}</strong>
  <SelectLazy v-model="deviceId" :provide-data="provideDataDevice" label="Device Id"/>
  <SelectLazy v-model="messageType" :provide-data="provideDataMessageType" label="Message type"/>

</template>
