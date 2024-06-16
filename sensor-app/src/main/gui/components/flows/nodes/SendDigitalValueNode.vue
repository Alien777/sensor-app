<script lang="ts" setup>
import {deviceApi} from "~/composables/api/DeviceApi";
import {configUtilsApi} from "~/composables/api/ConfigUtilsApi";
import SelectLazy from "~/components/common/SelectLazy.vue";
import {onMounted, watch} from "vue";
import {useVueFlow} from "@vue-flow/core";

const {updateNode} = useVueFlow()
const runtimeConfig = useRuntimeConfig();
const {getAllDevice} = deviceApi(runtimeConfig);
const {getDigitalPins} = configUtilsApi(runtimeConfig);

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
  getDigitalPins(deviceId.value).then(v => {
    value(v);
  })
}

const deviceId = ref(null);
const pin = ref(null);
const valueVariable = ref(null);

onMounted(() => {
  if (!props.sensor) {
    return;
  }
  deviceId.value = props.sensor.deviceId
  pin.value = props.sensor.pin
  valueVariable.value = props.sensor.valueVariable
})

watch(deviceId, () => {
  handleUpdate();
})
watch(pin, () => {
  handleUpdate();
})
watch(valueVariable, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {
    sensor: {
      deviceId: deviceId,
      pin: pin,
      valueVariable: valueVariable
    }
  } as any)
}
</script>

<template>
  <SelectLazy v-model="deviceId" :provide-data="provideDataDevice" label="Device id"/>
  <SelectLazy v-model="pin" :provide-data="provideDataPins" label="Digital pin"/>
  <q-input v-model="valueVariable" label="Variable" maxlength="40"/>
</template>
