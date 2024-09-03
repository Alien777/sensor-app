<script lang="ts" setup>
import {deviceApi} from "~/composables/api/DeviceApi";
import {configUtilsApi} from "~/composables/api/ConfigUtilsApi";
import SelectLazy from "~/components/common/SelectLazy.vue";
import {onMounted, watch} from "vue";
import {useVueFlow} from "@vue-flow/core";

const {updateNode} = useVueFlow()
const runtimeConfig = useRuntimeConfig();
const {getAllDevice} = deviceApi(runtimeConfig);
const {getPwmsPins} = configUtilsApi(runtimeConfig);

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
  getPwmsPins(deviceId.value).then(v => {
    value(v);
  })
}

const deviceId = ref(null);
const gpio = ref(null);
const valueVariable = ref(null);
const durationVariable = ref(null);

onMounted(() => {
  if (!props.sensor) {
    return;
  }
  deviceId.value = props.sensor.deviceId
  gpio.value = props.sensor.gpio
  valueVariable.value = props.sensor.valueVariable
  durationVariable.value = props.sensor.durationVariable
})

watch(deviceId, () => {
  handleUpdate();
})
watch(gpio, () => {
  handleUpdate();
})
watch(valueVariable, () => {
  handleUpdate();
})
watch(durationVariable, () => {
  handleUpdate();
})
function handleUpdate() {
  updateNode(props.id, {
    sensor: {
      deviceId: deviceId,
      gpio: gpio,
      valueVariable: valueVariable,
      durationVariable: durationVariable
    }
  } as any)
}
</script>

<template>
  <SelectLazy v-model="deviceId" :provide-data="provideDataDevice" label="Device Key"/>
  <SelectLazy v-model="gpio" :provide-data="provideDataPins" label="PWM pin"/>
  <q-input v-model="valueVariable" label="Value variable" maxlength="40"/>
  <q-input v-model="durationVariable" label="Duration variable" maxlength="40"/>
</template>
