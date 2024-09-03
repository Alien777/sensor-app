<script lang="ts" setup>
import {deviceApi} from "~/composables/api/DeviceApi";
import SelectLazy from "~/components/common/SelectLazy.vue";
import {watch, onMounted, ref, defineModel} from "vue";
import {useVueFlow} from "@vue-flow/core";
import {configUtilsApi} from "~/composables/api/ConfigUtilsApi";

const {updateNode} = useVueFlow()
const runtimeConfig = useRuntimeConfig();
const {getAllDevice} = deviceApi(runtimeConfig);
const {getMessageTypes} = configUtilsApi(runtimeConfig);
const deviceId = ref(null);
const payloadType = ref(null);


const props = defineProps({
  id: {
    type: String,
    required: true,
  },
  sensor: {
    type: Object as () => any,
    required: false,
  },
})

onMounted(() => {
  if (!props.sensor) {
    return;
  }
  deviceId.value = props.sensor.deviceId
  payloadType.value = props.sensor.payloadType
})

const provideDataDevice = (value: any) => {
  getAllDevice(false).then(v => {
    value(v.map(a => ({
      "value": a.id,
      "label": a.name,
    })));
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
watch(payloadType, () => {
  handleUpdate();
})

watch(deviceId, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {
    sensor: {
      deviceId: deviceId,
      payloadType: payloadType
    }
  } as any)
}
</script>

<template>
  <SelectLazy v-model="deviceId" :provide-data="provideDataDevice" label="Device Id"/>
  <SelectLazy v-model="payloadType" :provide-data="provideDataMessageType" label="Payload Type"/>
</template>
