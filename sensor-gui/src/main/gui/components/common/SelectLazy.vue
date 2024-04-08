<script setup lang="ts">
import {ref, defineEmits, defineProps, onMounted, onBeforeMount, watch, defineModel} from "vue";

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
})
const setOptions = (value: any) => {
  options.value = value;
}
const model = defineModel()

const options = ref(null)
props.provideData(setOptions);
const filter = (val: any, update: any, abort: any) => {
  update(() => {
    props.provideData(setOptions);
  })
}
</script>

<template>
  <div class="q-gutter-md row items-start">
    <q-select
        v-model="model"
        :label="props.label"
        :options="options"
        @filter="filter"
        emit-value
        map-options
        style="width: 100%">
      <template v-slot:no-option>
        <q-item>
          <q-item-section class="text-grey">
            No results
          </q-item-section>
        </q-item>
      </template>
    </q-select>
  </div>
</template>

<style scoped lang="css">

</style>