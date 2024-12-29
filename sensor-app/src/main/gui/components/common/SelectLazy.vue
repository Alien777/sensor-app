<script setup lang="ts">

const emit = defineEmits(['update:modelValue']);

const props = defineProps({
  label: {
    type: String,
    required: true,
  },
  provideData: {
    type: Function,
    required: true,
  },
});

const setOptions = (value: any) => {
  options.value = value;
};

const model = ref(null); // Zamiana na ref do zarządzania modelem
const options = ref<any[]>([]); // Używanie tablicy jako listy opcji

props.provideData(setOptions);

const filter = (query: string) => {
  props.provideData(setOptions);
};
</script>

<template>
  <div class="row items-start" style="width: 100%;">
    <el-select
        :v-model="model"
        :placeholder="props.label"
        filterable
        :filter-method="filter"
        style="width: 100%;">
      <el-option
          v-for="option in options"
          :key="option.value"
          :label="option.label"
          :value="option.value">
      </el-option>
      <template v-if="options.length === 0" v-slot="scope">
        <div class="text-grey" style="padding: 10px;">No results</div>
      </template>
    </el-select>
  </div>
</template>

<style scoped>
/* Możesz dodać tutaj swoje style */
</style>
