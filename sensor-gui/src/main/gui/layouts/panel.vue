<template>
  <q-layout view="hHh lpR fFf">
    <q-header class="bg-primary text-white">
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar>
            <img src="https://cdn.quasar.dev/logo-v2/svg/logo-mono-white.svg">
          </q-avatar>
          Sensor
        </q-toolbar-title>
        <q-btn dense flat round icon="menu" @click="toggleRightDrawer"/>
      </q-toolbar>
    </q-header>
    <q-drawer show-if-above v-model="rightDrawerOpen" side="right" elevated>
      <aside class="q-drawer q-drawer--right q-drawer--bordered q-drawer--standard fixed"
             style="width: 300px; transform: translateX(0px);  bottom: 50px;">
        <div class="q-drawer__content fit scroll">
          <div class="q-scrollarea" style="height: calc(100% - 204px); margin-top: 204px;">
            <div class="q-scrollarea__container scroll relative-position fit hide-scrollbar">
              <div class="q-scrollarea__content absolute"><!---->
                <div class="q-item__label q-item__label--header">Right Drawer</div>
                <div class="text-grey" style="padding: 25px 16px 16px;"><p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p>
                  <p><em>Right Drawer has intended scroll</em></p></div>
              </div><!----></div><!---->
          </div>

          <UserInfoPanel/>
        </div>
      </aside>
    </q-drawer>
    <q-page-container>
      <NuxtPage></NuxtPage>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import {ref} from 'vue'
import UserInfoPanel from "~/components/UserInfoPanel.vue";

const rightDrawerOpen = ref(false)

const toggleRightDrawer = () => {
  rightDrawerOpen.value = !rightDrawerOpen.value
}

</script>
