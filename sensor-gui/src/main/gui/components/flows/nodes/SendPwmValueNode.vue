<script lang="ts" setup>
import {deviceApi} from "~/composables/api/DeviceApi";
import {ConfigUtilsApi} from "~/composables/api/ConfigUtilsApi";
import SelectLazy from "~/components/common/SelectLazy.vue";
import {watch} from "vue";
import {useVueFlow} from "@vue-flow/core";

const {updateNode} = useVueFlow()
const runtimeConfig = useRuntimeConfig();
const {getAllDevice} = deviceApi(runtimeConfig);
const {getPwmsPins} = ConfigUtilsApi(runtimeConfig);

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

const provideDataPins = (value: any) => {
  if (deviceId.value == null) {
    return;
  }
  getPwmsPins(deviceId.value).then(v => {
    value(v);
  })
}

const deviceId = ref(null);
const pin = ref(null);
const valueVariable = ref(null);


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
  })
}
</script>

<template>
  <strong>{{ props.id }}</strong>
  <SelectLazy v-model="deviceId" :provide-data="provideDataDevice" label="Device Key"/>
  <SelectLazy v-model="pin" :provide-data="provideDataPins" label="PWM pin"/>
  <SelectLazy v-model="valueVariable" :provide-data="provideDataPins" label="Variable"/>

</template>
