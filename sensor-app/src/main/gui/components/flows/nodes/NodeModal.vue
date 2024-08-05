<script lang="ts" setup>
import {ref} from "vue"

//* cheat code for dialog, because when dialog close then slot's default  doesn't appear again
import {onClickOutside} from '@vueuse/core'
import type {Node, NodeDraggable} from "~/composables/api/StructureApp";

const dialog = ref(true);
const isOpen = ref(false);
const target = ref(null)

const props = defineProps({
  node: {
    type: Object as () => Node | NodeDraggable,
    required: true,
  },
  id: {
    type: Object,
    required: false,
  },
  nodeDefault: {
    type: Object as () => NodeDraggable,
    required: true,
  },
  deleteNodeFunction: {
    type: Object as () => any,
    required: true,
  },
  sensor: {
    type: Object as () => any,
    required: false,
  },
})

const open = () => {
  isOpen.value = true
}
const close = () => {
  if (isOpen.value === true) {
    setTimeout(() => {
      isOpen.value = false
    }, 50);
  }

}
onClickOutside(target, event => {
  let srcElement = event.target as HTMLDivElement;
  if (srcElement.className.includes("q-dialog__backdrop")) {
    close()
  }
})
</script>
<template>
  <div>
    <p style="margin: 0 0 4px;"><strong> {{ props.nodeDefault.readableName }}</strong></p>
    <p style="margin: 0 0 4px;">{{ props.id }}</p>
    <hr>
    <q-btn no-caps   size="xs" icon="edit" color="primary" @click="open"/>
    <q-btn no-caps  v-if="props.nodeDefault.description" size="xs" icon="info" color="info">
      <q-tooltip class="bg-purple text-body1" :offset="[10, 10]">
        {{ props.nodeDefault.description }}
      </q-tooltip>
    </q-btn>
    <q-btn no-caps  @click="()=>  {
       $q.dialog({
        title: 'Confirm',
        message: `Would you like to delete node: \'${props.nodeDefault.readableName}\'`,
        cancel: true,
        dark: true,
        color: 'red',
      }).onOk(() => {
      deleteNodeFunction(props.id)
      });
      }" size="xs" icon="delete" color="negative">
    </q-btn>
    <q-dialog
        id="settingsID"
        persistent
        position="right"
        v-model="dialog"
        :style="`display: ${isOpen ? 'blog' : 'none'}`">
      <div style="width: 500px; height: 100%" ref="target">
        <q-card style="width: 100%; height: 100%">
          <q-card-actions>
            <q-btn size="xs" style="z-index: 20000" icon="close" color="primary" @click="close"/>
          </q-card-actions>
          <q-card-section>
            <p style="margin: 0 0 4px;"><strong> {{ props.nodeDefault.readableName }}</strong></p>
            <p style="margin: 0 0 4px;">{{ props.id }}</p>
          </q-card-section>
          <q-card-section>
            <slot ref="test" name="default"></slot>
          </q-card-section>
        </q-card>
      </div>
    </q-dialog>
  </div>
</template>
