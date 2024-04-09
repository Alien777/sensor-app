<script setup lang="ts">
import {defineProps, ref} from 'vue';
import {deviceApi} from "~/composables/api/DeviceApi";
import {type DeviceConfigSaveT, type DeviceConfigT, type DeviceT, formatTime} from "~/composables/api/StructureApp";

const MonacoEditorCustom = defineAsyncComponent(() => import('./MonacoEditorCustom.vue'));

const runtimeConfig = useRuntimeConfig();
const {getDeviceConfig, saveDeviceConfig, getAllConfigs, activateConfig} = deviceApi(runtimeConfig);

const splitterHigh = 275;
const props = defineProps({
  device: {
    type: Object as () => DeviceT,
    required: true
  },
  onChange: {
    type: Function as PropType<() => void>,
    required: true
  }
});
const currentConfigDevice = ref(null);
const currentConfigEditRef = ref(null);
const allDevicesConfigsRef = ref(null);
const fetch = async () => {
  if (props.device?.version) {
    const currentConfigDownload = await getDeviceConfig(props.device.id);
    currentConfigDevice.value = currentConfigDownload;
    currentConfigEditRef.value = currentConfigDownload;
    const allDevicesConfigsDownload = await getAllConfigs(props.device.id);
    allDevicesConfigsRef.value = allDevicesConfigsDownload;
  }
}


onMounted(() => fetch())
const generateConfig: any = (currentConfig: DeviceConfigT) => {
  return {
    validate: true,
    schemas: [{
      uri: "http://myserver/schema.json",
      fileMatch: ['*'],
      schema: JSON.parse(currentConfig.schema)
    }]
  };
}
const saveConfigAction = async () => {
  const save: DeviceConfigSaveT = {
    config: currentConfigEditRef.value.config,
    version: currentConfigEditRef.value.forVersion
  };
  await saveDeviceConfig(props.device?.id, save).then(value => props.onChange());
  getAllConfigs(props.device.id).then(v => {
    allDevicesConfigsRef.value = v;
  });
}

const activateConfigAction = async () => {
  await activateConfig(props.device?.id, currentConfigEditRef.value.id);
  await getDeviceConfig(props.device.id).then(value => {
    currentConfigDevice.value = value;
  })
  getAllConfigs(props.device.id).then(v => {
    allDevicesConfigsRef.value = v;
  });

}

const selectConfig = (config: DeviceConfigT) => {
  currentConfigEditRef.value = config;
}

</script>

<template>
  <div v-if="currentConfigDevice && currentConfigDevice.config">
    <div class="text-h5"><p>Editing: {{ props.device.name }}</p></div>
    <hr>
    <div class="q-pb-md">
      <q-btn-group spread>
        <q-btn icon="save" class="bg-green-1" @click="saveConfigAction">Save as new config</q-btn>
        <q-btn icon="check" @click="activateConfigAction"
               v-if="currentConfigEditRef.forVersion===device.version && currentConfigEditRef.id!==currentConfigDevice.id"
               class="bg-green-2">Activate config
        </q-btn>
      </q-btn-group>
    </div>
    <q-splitter v-model="splitterHigh" unit="px" style="height: calc(100vh - 150px)">
      <template v-slot:before>
        <q-list padding>
          <q-item v-for="config in allDevicesConfigsRef"
                  clickable
                  v-ripple
                  @click="()=>selectConfig(config)"
                  :active="currentConfigEditRef.id===config.id">

            <q-item-section avatar>
              <q-item-label>{{ `id: ${config.id}` }}</q-item-label>
              <s v-if="config.forVersion!==device.version">
                <q-item-label>{{ `ID: ${config.forVersion}` }}
                  <q-tooltip>
                    This version of config is doesn't compatibility with device firmware version
                  </q-tooltip>
                </q-item-label>
              </s>
              <q-item-label v-else>{{ `ver.: ${config.forVersion}` }}</q-item-label>
            </q-item-section>
            <q-item-section side top>
              <q-item-label caption>{{ formatTime(config.time as Date) }}</q-item-label>
              <q-item-label>
                <q-icon v-if="currentConfigDevice.id===config.id" name="check" color="green">
                  <q-tooltip>
                    Current activated. This configuration will be send to device.
                  </q-tooltip>
                </q-icon>
<!--                <q-icon v-if="!config.isCorrect" name="close" color="red">-->
<!--                  <q-tooltip>-->
<!--                    This configuration is doesn't correct with schema. Not possible to active it.-->
<!--                  </q-tooltip>-->
<!--                </q-icon>-->
              </q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </template>
      <template v-slot:after>
        <ClientOnly>
          <MonacoEditorCustom :jsonDefaultConfig="generateConfig(currentConfigEditRef)"
                              v-model="currentConfigEditRef.config"
                              lang="json"
                              class="editor">
          </MonacoEditorCustom>
        </ClientOnly>
      </template>
    </q-splitter>
  </div>
  <div v-else>
    <div class="text-h5"><p>Device {{ props.device.name }} not configured compleate</p></div>
    <hr>
    <NotSetUpVersion :device="props.device" ></NotSetUpVersion>
  </div>

</template>

<style lang="css" scoped>
.editor {
  width: 100%;

}
</style>