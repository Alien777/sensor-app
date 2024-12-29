<script lang="ts" setup>
import {deviceApi} from "~/composables/api/DeviceApi";
import {configUtilsApi} from "~/composables/api/ConfigUtilsApi";
import SelectLazy from "~/components/common/SelectLazy.vue";
import {onMounted, watch} from "vue";
// import {useVueFlow} from "@vue-flow/core";

// const {updateNode} = useVueFlow()
const runtimeConfig = useRuntimeConfig();
const {getAllDevice} = deviceApi(runtimeConfig);
const {getAnalogsPins} = configUtilsApi(runtimeConfig);

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
const provideDataDevice = (value: any) => {
  getAllDevice(false).then(v => {
    value(v.map(a => ({
      "value": a.id,
      "label": a.name,
    })));
  })
}

const provideDataPins = (value: any) => {
  if (deviceId.value == null) {
    return;
  }
  getAnalogsPins(deviceId.value).then(v => {
    value(v);
  })
}

const deviceId = ref(null);
const gpio = ref(null);

onMounted(() => {
  if (!props.sensor) {
    return;
  }
  deviceId.value = props.sensor.deviceId
  gpio.value = props.sensor.gpio
})

watch(deviceId, () => {
  handleUpdate();
})
watch(gpio, () => {
  handleUpdate();
})

function handleUpdate() {
  // updateNode(props.id, {
  //   sensor: {
  //     deviceId: deviceId,
  //     gpio: gpio
  //   }
  // } as any)
}
</script>

<template>
  <SelectLazy v-model="deviceId" :provide-data="provideDataDevice" label="Device Key"/>
  <SelectLazy v-model="gpio" :provide-data="provideDataPins" label="gpio"/>
</template>
