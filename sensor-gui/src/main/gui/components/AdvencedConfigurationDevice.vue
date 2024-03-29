<script setup lang="ts">
import {defineProps, ref} from 'vue';
import {deviceApi} from "~/composables/api/DeviceApi";
import {type DeviceConfigSaveT, type DeviceConfigT, type DeviceT, formatTime} from "~/composables/api/StructureApp";

const MonacoEditorCustom = defineAsyncComponent(() => import('./MonacoEditorCustom.vue'));

const runtimeConfig = useRuntimeConfig();
const {getDeviceConfig, saveDeviceConfig, getAllConfigs, activateConfig} = deviceApi(runtimeConfig);

const splitterHigh = 70;
const props = defineProps({
  device: {
    type: Object as () => DeviceT,
    required: true
  }
});

const currentConfigDownload = await getDeviceConfig(props.device.id);
const allDevicesConfigsDownload = await getAllConfigs(props.device.id);
const currentConfigDevice = ref(currentConfigDownload);
const currentConfigEditRef = ref(currentConfigDownload);
const allDevicesConfigsRef = ref(allDevicesConfigsDownload);

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
  await saveDeviceConfig(props.device?.id, save);
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
  <p><strong>{{ device.name ? device.name : device.deviceKey }}</strong> You editing config
    id:<strong>{{ currentConfigEditRef.id }}</strong></p>
  <q-splitter v-model="splitterHigh" style="height: 400px">
    <template v-slot:before>
      <ClientOnly>

        <MonacoEditorCustom :jsonDefaultConfig="generateConfig(currentConfigEditRef)"
                            v-model="currentConfigEditRef.config"
                            lang="json"
                            class="editor">
          Loading...
        </MonacoEditorCustom>
      </ClientOnly>
    </template>
    <template v-slot:after>
      <div class="q-pa-md" style="max-width: 350px">

        <q-list separator padding>
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
                <q-icon v-if="!config.isCorrect" name="close" color="red">
                  <q-tooltip>
                    This configuration is doesn't correct with schema. Not possible to active it.
                  </q-tooltip>
                </q-icon>
              </q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </div>
    </template>
  </q-splitter>
  <q-btn @click="saveConfigAction">Save as new config</q-btn>
  <q-btn @click="activateConfigAction"
         v-if="currentConfigEditRef.forVersion===device.version && currentConfigEditRef.id!==currentConfigDevice.id"
         color="green"
         text-color="black">Activate config
  </q-btn>

</template>

<style lang="css" scoped>
.editor {
  width: 100%;
  height: calc(500px - 3em);
}
</style>