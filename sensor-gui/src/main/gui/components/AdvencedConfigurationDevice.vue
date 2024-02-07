<script setup lang="ts">
import {defineProps, ref} from 'vue';
import {deviceApi} from "~/composables/api/DeviceApi";
import {type DeviceConfigSaveT, type DeviceConfigT, type DeviceT, formatTime} from "~/composables/api/StructureApp";

const MonacoEditorCustom = defineAsyncComponent(() => import('./MonacoEditorCustom.vue'));

const runtimeConfig = useRuntimeConfig();
const {getDeviceConfig, saveDeviceConfig, getAllConfigs} = deviceApi(runtimeConfig);

const splitterHigh = 70;
const props = defineProps({
  device: {
    type: Object as () => DeviceT,
    required: true
  }
});

const currentConfig = await getDeviceConfig(props.device.id);
const allDevicesConfigs = await getAllConfigs(props.device.id);

const currentConfigEdit = ref(currentConfig);

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
const saveConfigAction = () => {
  const save: DeviceConfigSaveT = {
    config: currentConfigEdit.value.config,
    version: currentConfigEdit.value.forVersion
  };
  saveDeviceConfig(props.device?.id, save)
      .then(value => {
        // console.log(value);
      })
}

const selectConfig = (config: DeviceConfigT) => {
  currentConfigEdit.value = config;
}

</script>

<template>
  <p><strong>{{ device.name ? device.name : device.deviceKey }}</strong></p>
  <q-splitter v-model="splitterHigh" style="height: 400px">
    <template v-slot:before>
      <ClientOnly>
        <MonacoEditorCustom :jsonDefaultConfig="generateConfig(currentConfigEdit)" v-model="currentConfigEdit.config"
                            lang="json"
                            class="editor">
          Loading...
        </MonacoEditorCustom>
      </ClientOnly>
    </template>
    <template v-slot:after>
      <div class="q-pa-md" style="max-width: 350px">

        <q-list separator padding>
          <q-item v-for="config in allDevicesConfigs"
                  clickable
                  v-ripple
                  @click="()=>selectConfig(config)"
                  :active="currentConfigEdit.id===config.id">
            <q-item-section>
              <s v-if="config.forVersion!==device.version">
                <q-item-label>{{ `version: ${config.forVersion}` }}
                  <q-tooltip>
                    This version of config is doesn't compatibility with device firmware version
                  </q-tooltip>
                </q-item-label>
              </s>
              <q-item-label v-else>{{ `version: ${config.forVersion}` }}</q-item-label>
            </q-item-section>
            <q-item-section side top>
              <q-item-label caption>{{ formatTime(config.time as Date) }}</q-item-label>
              <q-item-label>
                <q-icon v-if="currentConfig.id===config.id" name="check" color="green">
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
  <q-btn v-if="currentConfigEdit.forVersion===device.version" color="green" text-color="black">Activate config</q-btn>

</template>

<style lang="css" scoped>
.editor {
  width: 100%;
  height: calc(500px - 3em);
}
</style>