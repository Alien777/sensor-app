<script lang="ts" setup>
import {deviceApi} from "~/composables/api/DeviceApi";
import SelectLazy from "~/components/common/SelectLazy.vue";
import {onMounted, ref, watch} from "vue";
// import {useVueFlow} from "@vue-flow/core";

// const {updateNode} = useVueFlow()
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
  // updateNode(props.id, {
  //   sensor: {
  //     deviceId: deviceId,
  //     gpio: gpio,
  //     frequency: frequency,
  //     resolution: resolution,
  //     duty: duty,
  //   }
  // } as any)
}
</script>

<template>
  <SelectLazy v-model="deviceId" :provide-data="provideDataDevice" label="Device Id"/>
  <el-input v-model="gpio" label="GPIO"/>
  <el-input v-model="frequency" label="Frequency"/>
  <el-input v-model="resolution" label="Resolution"/>
  <el-input v-model="duty" label="Initial duty"/>
</template>
