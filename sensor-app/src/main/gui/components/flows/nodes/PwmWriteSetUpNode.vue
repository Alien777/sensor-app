<script lang="ts" setup>
import {deviceApi} from "~/composables/api/DeviceApi";
import SelectLazy from "~/components/common/SelectLazy.vue";
import {watch, onMounted, ref, defineModel} from "vue";
import {useVueFlow} from "@vue-flow/core";
import {configUtilsApi} from "~/composables/api/ConfigUtilsApi";

const {updateNode} = useVueFlow()
const runtimeConfig = useRuntimeConfig();
const {getAllDevice} = deviceApi(runtimeConfig);

const deviceId = ref(null);
const gpio = ref(null);
const frequency = ref(null);
const resolution = ref(null);
const duty = ref(null);


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
  gpio.value = props.sensor.gpio
  frequency.value = props.sensor.frequency
  resolution.value = props.sensor.resolution
  duty.value = props.sensor.duty
})

const provideDataDevice = (value: any) => {
  getAllDevice(false).then(v => {
    value(v.map(a => ({
      "value": a.id,
      "label": a.name,
    })));
  })
}


watch(deviceId, () => {
  handleUpdate();
})

watch(gpio, () => {
  handleUpdate();
})

watch(frequency, () => {
  handleUpdate();
})

watch(resolution, () => {
  handleUpdate();
})

watch(duty, () => {
  handleUpdate();
})

function handleUpdate() {
  updateNode(props.id, {
    sensor: {
      deviceId: deviceId,
      gpio: gpio,
      frequency: frequency,
      resolution: resolution,
      duty: duty,
    }
  } as any)
}
</script>

<template>
  <SelectLazy v-model="deviceId" :provide-data="provideDataDevice" label="Device Id"/>
  <q-input v-model="gpio" label="GPIO"/>
  <q-input v-model="frequency" label="Frequency"/>
  <q-input v-model="resolution" label="Resolution"/>
  <q-input v-model="duty" label="Initial duty"/>
</template>
