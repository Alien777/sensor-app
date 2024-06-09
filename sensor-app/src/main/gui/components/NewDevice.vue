<script setup lang="ts">
import {computed, defineProps, ref} from 'vue'
import {deviceApi} from "~/composables/api/DeviceApi";
import {Notify, QSpinnerGears} from "quasar";
import SelectLazy from "~/components/common/SelectLazy.vue";

const props = defineProps({
  onChange: {
    type: Function as PropType<() => void>,
    required: true
  }
});

enum Enum {
  UPLOAD_NEW_FIRMWARE
}


const offlineStep = ["name_step", "wifi_config_step", "online_summary_step"]
const runtimeConfig = useRuntimeConfig();
const {initDevice, getVersions} = deviceApi(runtimeConfig);
const loading = ref(false)
const step = ref('name_step');
const typeOfStep = ref(Enum.UPLOAD_NEW_FIRMWARE);
const stepPath = ref(offlineStep);
const device_name = ref<string>();
const version = ref<string>('');
const wifi_ssid = ref<string>('');
const wifi_password = ref<string>('');

const isNameValid = computed(() => device_name.value != null && device_name.value.length >= 1);
const versionValid = computed(() => version.value != null && version.value.length >= 1);
const ssidValid = computed(() => wifi_ssid.value != null && wifi_ssid.value.length >= 1);

const canProceed = computed(() => {
  if (step.value === "name_step") return isNameValid.value && versionValid.value;
  if (step.value === "wifi_config_step") return ssidValid.value;
  return true;
});

const execute = (step: string) => {
}

const onFinish = (ref: any) => {
  loading.value = true;
  if (!wifi_password.value || !wifi_ssid.value || !device_name.value || !version.value) {
    Notify.create({
      type: 'error',
      message: 'Problem with send message. Data not valid'
    })
    loading.value = false;
    return;
  }
  initDevice(version.value, device_name.value, wifi_ssid.value, wifi_password.value)
      .then(success => {
        if (success) {
          ref.stepper.next();
        } else {
          Notify.create({
            type: 'error',
            message: `Problem with generate ${device_name.value}-${version.value}.zip`
          })
        }
      }).finally(() => {
    props.onChange();
    loading.value = false;
  });

};
const provideVersions = (value: any) => {
  getVersions().then((v) => {
    value(v);
  });
};
</script>

<template>
  <div class="q-pa-md">

    <q-stepper v-model="step" ref="stepper" color="primary" animated>
      <q-step name="name_step" title="Enter the device name" icon="settings" :done="step =='wifi_config_step'">

        <q-input label="Name of Device" v-model="device_name" maxlength="32" hint="example: Kitchen Sensor" clearable
                 :rules="[value => (value && value.length >= 1) || 'Minimal length is 1.']"
        ></q-input>

        <SelectLazy v-model="version" :provide-data="provideVersions" label="Version of device"/>
      </q-step>
      <q-step v-if="typeOfStep===Enum.UPLOAD_NEW_FIRMWARE" name="wifi_config_step" title="Enter the WiFi credential"
              icon="settings"
              :done="step =='online_summary_step'">
        <div v-if="!loading">
          <p>Credential WiFi</p>
          <q-input label="SSID" v-model="wifi_ssid" maxlength="32" hint="example: dom" clearable
          ></q-input>
          <q-input label="Password" v-model="wifi_password" maxlength="64" clearable
          ></q-input>
        </div>
        <div v-else class="flex flex-center">
          <q-spinner-gears size="50px" color="primary"/>
          <br>
          <p>Generate file. Please wait.</p>
        </div>
      </q-step>
      <q-step v-if="typeOfStep===Enum.UPLOAD_NEW_FIRMWARE" name="online_summary_step" title="Summary"
              icon="add_comment">
        <p>{{ `File was downloaded: ${device_name}-${version}.zip` }}</p>
        <p>{{ `Connect the device to the usb port` }}</p>
        <p>{{ 'Unzip it and run the upload.sh or upload.bat script' }}</p>
      </q-step>
      <template v-slot:navigation>
        <q-stepper-navigation>
          <q-btn
              v-if="step !== stepPath[stepPath.length-2] && step !== stepPath[stepPath.length-1]"
              @click="()=>{
            $refs.stepper.next();
            execute(step);
          }"
              :disabled="!canProceed"
              color="primary"
              label="Next"/>
          <q-btn v-if="!loading && step === stepPath[stepPath.length-2]" @click="()=>onFinish($refs) && loading"
                 :disabled="!canProceed"
                 color="primary" label="Finish"/>
          <q-btn v-if=" !loading && step !== stepPath[0] && step !== stepPath[stepPath.length-1]  && loading" flat
                 color="primary"
                 @click="$refs.stepper.previous()" label="Back"
                 class="q-ml-sm"/>
        </q-stepper-navigation>
      </template>
    </q-stepper>
  </div>
</template>

<style scoped lang="css">

</style>