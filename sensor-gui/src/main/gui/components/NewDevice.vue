<script setup lang="ts">
import {computed, defineProps, ref, watch} from 'vue'
import {deviceApi} from "~/composables/api/DeviceApi";
import {resourceApi} from "~/composables/api/ResourcesApi";
import {Notify} from "quasar";

const props = defineProps({
  onChange: {
    type: Function as PropType<() => void>,
    required: true
  }
});

enum Enum {
  LOCAL,
  OFFLINE
}


const offlineStep = ['choice_step', "name_step", "online_device_id_step", "online_summary_step"]
const localStep = ['choice_step', "name_step", "wifi_config", "local_wait", "local_summary_step"]

const runtimeConfig = useRuntimeConfig();
const {memberId} = authUtils(runtimeConfig);
const {saveDevice} = deviceApi(runtimeConfig);
const {mqttServer} = resourceApi(runtimeConfig);

const step = ref('choice_step');
const typeOfStep = ref(Enum.LOCAL);
const stepPath = ref(offlineStep);
const name = ref<string>('');
const id = ref<string>('');
const token = ref<string>('');
const ssid = ref<string>('');
const password = ref<string>('');
const server = ref<string>('');

// Computed properties for validation
const isNameValid = computed(() => name.value != null && name.value.length >= 1);
const isIdValid = computed(() => id.value != null && id.value.length === 12);

// Check if it's okay to proceed to the next step or to finish
const canProceed = computed(() => {
  if (step.value === "name_step") return isNameValid.value;
  if (step.value === "online_device_id_step" && typeOfStep.value === Enum.OFFLINE) return isIdValid.value;
  return true; // For any step beyond, adjust as necessary
});

const execute = (step: string) => {
  if (step == "local_wait") {
    saveDevice('', name.value).then(value => {
      token.value = value
    }).then(() => {
      props.onChange();
    })
  }
}
// Method to handle the finish action
const onFinish = (ref: any) => {
  mqttServer().then(value => server.value = value)
  if (typeOfStep.value === Enum.OFFLINE) {
    saveDevice(id.value, name.value).then(value => token.value = value).then(() => {
      ref.stepper.next();
      props.onChange();
    })
  }
  if (typeOfStep.value === Enum.LOCAL) {

    if (!ssid.value || !password.value || !token.value || !memberId() || !server) {
      Notify.create({
        type: 'error',
        message: 'Problem with send message. Data not valid'
      })
      return;
    }

    try {
      // Using Nuxt 3's useFetch to post data
      const {data, error} = useFetch('http://192.168.4.1/save', {
        method: 'POST',
        body: JSON.stringify({
          ssid: ssid.value,
          password: password.value,
          token: token.value,
          member_key: memberId(),
          server_ip: server.value,
        }),
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (error.value) {
        console.error('Error:', error.value);
        Notify.create({
          type: 'error',
          message: 'Problem with send message. Server not valid'
        })
      }
    } catch (error) {
      Notify.create({
        type: 'error',
        message: 'Problem with send message. Server not valid'
      })
    }
  }

  // Implement finishing actions here
};

watch(id, (newValue) => {
  const correctedValue = newValue.replace(/[^a-zA-Z0-9]/g, '').toUpperCase();
  if (newValue !== correctedValue) {
    id.value = correctedValue;
  }
});

</script>

<template>
  <div class="q-pa-md">
    <q-stepper v-model="step" ref="stepper" color="primary" animated>
      <q-step name="choice_step" title="Choice step" icon="settings" :done="step =='choice_step'">
        <q-btn @click="()=>{
          stepPath=localStep;
          typeOfStep=Enum.LOCAL
          $refs.stepper.next();
        }">Configure in local
        </q-btn>
        <q-btn @click="()=>{
          stepPath=offlineStep;
          typeOfStep=Enum.OFFLINE
          $refs.stepper.next();
        }">Configure offline
        </q-btn>
      </q-step>
      <q-step name="name_step" title="Enter the device name" icon="settings" :done="step =='name_step'">
        <q-input label="Name of Device" v-model="name" maxlength="32" hint="example: Kitchen Sensor" clearable
                 :rules="[value => (value && value.length >= 1) || 'Minimal length is 1.']"
        ></q-input>
      </q-step>
      <q-step v-if="typeOfStep===Enum.OFFLINE" name="online_device_id_step" title="Enter the device ID" icon="settings"
              :done="step =='online_device_id_step'">
        <q-input label="Identyfity device" v-model="id" maxlength="12" hint="example: 7C9EBDF513CC" clearable
                 :rules="[value => (value && value.length === 12) || 'Minimal length is 12.']"
        ></q-input>
      </q-step>
      <q-step v-if="typeOfStep===Enum.LOCAL" name="wifi_config" title="Wifi config" icon="add_comment">
        <p>Wifi of device</p>
        <q-input label="SSID" v-model="ssid" maxlength="32" hint="example: dom" clearable
        ></q-input>
        <q-input label="Password" v-model="password" maxlength="64" clearable
        ></q-input>
      </q-step>
      <q-step v-if="typeOfStep===Enum.OFFLINE" name="online_summary_step" title="Summary" icon="add_comment">
        <p>This configuration please entry into device panel configuration.</p>
        <p><em>Member ID: </em>{{ memberId() }}</p>
        <p><em>Token: </em>{{ token }}</p>
        <p><em>Server IP: </em>{{ server }}</p>
      </q-step>
      <q-step v-if="typeOfStep===Enum.LOCAL" name="local_wait" title="Information" icon="add_comment">
        <p>Please connect by wifi to Access Point of device</p>
      </q-step>
      <q-step v-if="typeOfStep===Enum.LOCAL" name="local_summary_step" title="Summary" icon="add_comment">
        <p>Esp will be configured. Please wait for connect device to fill ID</p>
      </q-step>
      <template v-if="step !== stepPath[stepPath.length-1]" v-slot:navigation>
        <q-stepper-navigation>
          <q-btn
              v-if="step !== stepPath[stepPath.length-2] && step !== stepPath[stepPath.length-1] && step !== stepPath[0]"
              @click="()=>{
            $refs.stepper.next();
            execute(step);
          }"
              :disabled="!canProceed"
              color="primary"
              label="Next"/>
          <q-btn v-if="step === stepPath[stepPath.length-2]" @click="()=>onFinish($refs)" :disabled="!canProceed"
                 color="primary" label="Finish"/>
          <q-btn v-if="step !== stepPath[0]" flat color="primary" @click="$refs.stepper.previous()" label="Back"
                 class="q-ml-sm"/>
        </q-stepper-navigation>
      </template>
    </q-stepper>
  </div>
</template>

<style scoped lang="css">

</style>