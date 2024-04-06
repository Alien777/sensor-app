<script setup lang="ts">
import {computed, defineProps, ref, watch} from 'vue'
import {deviceApi} from "~/composables/api/DeviceApi";

const props = defineProps({
  onChange: {
    type: Function as PropType<() => void>,
    required: true
  }
});
const runtimeConfig = useRuntimeConfig();
const {memberId} = authUtils(runtimeConfig);
const {saveDevice} = deviceApi(runtimeConfig);
const step = ref(1);
const name = ref<string>('');
const id = ref<string>('');
const token = ref<string>('');

// Computed properties for validation
const isNameValid = computed(() => name.value != null && name.value.length >= 1);
const isIdValid = computed(() => id.value != null && id.value.length === 12);

// Check if it's okay to proceed to the next step or to finish
const canProceed = computed(() => {
  if (step.value === 1) return isNameValid.value;
  if (step.value === 2) return isIdValid.value;
  return true; // For any step beyond, adjust as necessary
});

// Method to handle the finish action
const onFinish = (ref: any) => {
  saveDevice(id.value, name.value).then(value => token.value = value).then(() => {
    ref.stepper.next();
    props.onChange();
  })
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
    <q-stepper
        v-model="step"
        ref="stepper"
        color="primary"
        animated
    >
      <q-step
          :name="1"
          title="Enter the device name"
          icon="settings"
          :done="step > 1"
      >
        <q-input label="Name of Device" v-model="name" maxlength="32" hint="example: Kitchen Sensor" clearable
                 :rules="[value => (value && value.length >= 1) || 'Minimal length is 1.']"
        ></q-input>
      </q-step>

      <q-step
          :name="2"
          title="Enter the device ID"
          icon="settings"
          :done="step > 2"
      >

        <q-input label="Identyfity device" v-model="id" maxlength="12" hint="example: 7C9EBDF513CC" clearable
                 :rules="[value => (value && value.length === 12) || 'Minimal length is 12.']"
        ></q-input>
      </q-step>
      <q-step
          :name="3"
          title="Summary"
          icon="add_comment"
      >
        <p>This configuration please entry into device panel configuration.</p>
        <p><em>Member ID: </em>{{ memberId() }}</p>
        <p><em>Token: </em>{{ token }}</p>
      </q-step>

      <template v-if="step!==3" v-slot:navigation>
        <q-stepper-navigation>
          <q-btn v-if="step!==2" @click="$refs.stepper.next()" :disabled="!canProceed" color="primary"
                 label="Next"/>
          <q-btn v-else @click="()=>onFinish($refs)" :disabled="!canProceed" color="primary" label="Finish"/>
          <q-btn v-if="step > 1" flat color="primary" @click="$refs.stepper.previous()" label="Back" class="q-ml-sm"/>
        </q-stepper-navigation>
      </template>

    </q-stepper>
  </div>
</template>

<style scoped lang="css">

</style>