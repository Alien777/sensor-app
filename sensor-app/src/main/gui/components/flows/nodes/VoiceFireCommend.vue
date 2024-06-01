<script lang="ts" setup>
import {deviceApi} from "~/composables/api/DeviceApi";
import {onMounted, ref, watch} from "vue";
import {useVueFlow} from "@vue-flow/core";
import {configUtilsApi} from "~/composables/api/ConfigUtilsApi";

const {updateNode} = useVueFlow()
const runtimeConfig = useRuntimeConfig();
const {getAllDevice} = deviceApi(runtimeConfig);
const {getMessageTypes} = configUtilsApi(runtimeConfig);
const commends = ref(null);


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
  commends.value = props.sensor.commends
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
  if (commends.value == null) {
    return;
  }
  getMessageTypes(commends.value).then(v => {
    value(v);
  })
}


function handleUpdate() {
  updateNode(props.id, {
    sensor: {
      commends: commends
    }
  } as any)
}
</script>

<template>
  <q-input v-model="commends"  type="textarea" label="Commends" hint="Open windows; Is to hot"/>
</template>
