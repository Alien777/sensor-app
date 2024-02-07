<template>
    <div ref="editorElement" class="editor-container">
      <slot v-if="isLoading"></slot>
    </div>
</template>

<script lang="ts">
import {onMounted, onBeforeUnmount, ref, watch, defineComponent} from 'vue';
import * as Monaco from 'monaco-editor';

export default defineComponent({
  name: 'MonacoEditorCustom',
  props: {
    modelValue: {
      type: String,
      required: false,
      default: ''
    },
    lang: {
      type: String,
      required: false,
      default: 'json'
    },
    options: {
      type: Object,
      required: false,
      default: () => ({})
    },
    jsonDefaultConfig: {
      type: Object,
      required: false,
      default: null
    }
  },
  emits: ['update:modelValue'],
  setup(props, {emit}) {
    const editorElement = ref<HTMLElement | null>(null);
    const isLoading = ref(true);
    let editor: Monaco.editor.IStandaloneCodeEditor | null = null;
    let model: Monaco.editor.ITextModel | null = null;

    const defaultOptions: Monaco.editor.IStandaloneEditorConstructionOptions = {
      automaticLayout: true,
    };

    onMounted(() => {
      if (editorElement.value) {
        const mergedOptions = {...defaultOptions, ...props.options};
        if (props.lang === 'json') {
          Monaco.languages.json.jsonDefaults.setDiagnosticsOptions(props.jsonDefaultConfig);
        }
        editor = Monaco.editor.create(editorElement.value, mergedOptions);
        model = Monaco.editor.createModel(props.modelValue, props.lang);
        editor.setModel(model);

        editor.onDidChangeModelContent(() => {
          emit('update:modelValue', editor.getValue());
        });

        isLoading.value = false;
      }
    });


    watch(() => props.modelValue, (newValue) => {
      if (editor && newValue !== editor.getValue()) {
        editor.setValue(newValue);
      }
    });

    watch(() => props.jsonDefaultConfig, (newValue) => {
      if (props.lang === 'json') {
        Monaco.languages.json.jsonDefaults.setDiagnosticsOptions(newValue);
      }
    });

    onBeforeUnmount(() => {
      editor?.dispose();
      model?.dispose();
    });

    return {editorElement, isLoading};
  }
});
</script>

<style lang="css" scoped>
.editor-container {
  height: 100%;
  width: 100%;
}
</style>
