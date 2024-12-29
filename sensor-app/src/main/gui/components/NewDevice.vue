<script setup lang="ts">
import { deviceApi } from '~/composables/api/DeviceApi';
import { ElNotification, ElLoading } from 'element-plus';
import SelectLazy from '~/components/common/SelectLazy.vue';

const props = defineProps({
  onChange: {
    type: Function as PropType<() => void>,
    required: true,
  },
});

enum Enum {
  UPLOAD_NEW_FIRMWARE,
}

const offlineStep = ['name_step', 'wifi_config_step', 'online_summary_step'];
const runtimeConfig = useRuntimeConfig();
const { initDevice, getVersions } = deviceApi(runtimeConfig);
const loading = ref(false);
const step = ref('name_step');
const typeOfStep = ref(Enum.UPLOAD_NEW_FIRMWARE);
const stepPath = ref(offlineStep);
const device_name = ref<string>();
const version = ref<string>('');
const wifi_ssid = ref<string>('');
const wifi_password = ref<string>('');
const ap_password = ref<string>('');

const isNameValid = computed(() => device_name.value != null && device_name.value.length >= 1);
const versionValid = computed(() => version.value != null && version.value.length >= 1);
const ssidValid = computed(() => wifi_ssid.value != null && wifi_ssid.value.length >= 1);

const canProceed = computed(() => {
  if (step.value === 'name_step') return isNameValid.value && versionValid.value;
  if (step.value === 'wifi_config_step') return ssidValid.value;
  return true;
});

const execute = (step: string) => {};

const onFinish = async (ref: any) => {
  loading.value = true;
  if (!wifi_password.value || !wifi_ssid.value || !device_name.value || !version.value || !ap_password.value) {
    ElNotification({
      type: 'error',
      message: 'Problem with sending the message. Data not valid.',
    });
    loading.value = false;
    return;
  }
  try {
    const success = await initDevice(
        version.value,
        device_name.value,
        wifi_ssid.value,
        wifi_password.value,
        ap_password.value
    );
    if (success) {
      ref.stepper.next();
    } else {
      ElNotification({
        type: 'error',
        message: `Problem with generating ${device_name.value}-${version.value}.zip`,
      });
    }
  } finally {
    props.onChange();
    loading.value = false;
  }
};

const provideVersions = async (value: any) => {
  const v = await getVersions();
  value(v);
};
</script>

<template>
  <div class="el-pa-md">
    <el-steps :active="stepPath.indexOf(step)" finish-status="success">
      <!-- Step 1: Device Name -->
      <el-step
          :status="step === 'name_step' ? 'process' : 'finish'"
          title="Enter the device name"
          icon="el-icon-setting"
      >
        <div>
          <el-input
              v-model="device_name"
              placeholder="Name of Device"
              maxlength="32"
              clearable
              :rules="[value => (value && value.length >= 1) || 'Minimal length is 1.']"
          />
          <SelectLazy v-model="version" :provide-data="provideVersions" label="Version of device" />
        </div>
      </el-step>

      <!-- Step 2: WiFi Configuration -->
      <el-step
          v-if="typeOfStep === Enum.UPLOAD_NEW_FIRMWARE"
          :status="step === 'wifi_config_step' ? 'process' : 'wait'"
          title="Enter the WiFi credential"
          icon="el-icon-wifi"
      >
        <div v-if="!loading">
          <el-input
              v-model="wifi_ssid"
              placeholder="SSID"
              maxlength="32"
              clearable
          />
          <el-input
              v-model="wifi_password"
              type="password"
              placeholder="WiFi Password"
              maxlength="64"
              clearable
          />
          <el-input
              v-model="ap_password"
              type="password"
              placeholder="AP Password"
              maxlength="64"
              clearable
          />
        </div>
        <div v-else class="flex-center">
          <el-loading :loading="loading" text="Generating file, please wait..." />
        </div>
      </el-step>

      <!-- Step 3: Summary -->
      <el-step
          v-if="typeOfStep === Enum.UPLOAD_NEW_FIRMWARE"
          :status="step === 'online_summary_step' ? 'process' : 'wait'"
          title="Summary"
          icon="el-icon-document"
      >
        <div>
          <p>{{ `File was downloaded: ${device_name}-${version}.zip` }}</p>
          <p>Connect the device to the USB port.</p>
          <p>Unzip it and run the upload.sh or upload.bat script.</p>
        </div>
      </el-step>
    </el-steps>

    <!-- Navigation -->
    <div class="navigation">
      <el-button
          v-if="step !== stepPath[stepPath.length - 1]"
          :disabled="!canProceed"
          type="primary"
          @click="() => stepPath.indexOf(step) !== stepPath.length - 1 && execute(step)"
      >
        Next
      </el-button>
      <el-button
          v-if="step === stepPath[stepPath.length - 2]"
          :disabled="!canProceed"
          type="success"
          @click="() => onFinish($refs)"
      >
        Finish
      </el-button>
      <el-button
          v-if="step !== stepPath[0]"
          type="info"
          @click="$refs.stepper.previous()"
      >
        Back
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
