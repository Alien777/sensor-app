<template>
  <div>
    <el-button  no-caps  color="green" v-if="!socket" @click="startRecording">Start voice commend</el-button>
    <el-button no-caps  color="red" v-if="socket" @click="stopRecording">Stop voice commend</el-button>
  </div>
</template>

<script setup>
import {onBeforeUnmount, ref} from 'vue'
import {storageUtils} from "~/composables/StorageUtils";

const runtimeConfig = useRuntimeConfig();

const {getToken} = storageUtils(runtimeConfig);
const SOCKET_URL = runtimeConfig.public.wsApi + '/socket/audio-commend'

// Ref dla WebSocket
const socket = ref(null)
const audioContext = ref(null)
const processor = ref(null)
const input = ref(null)
const stream = ref(null)

const startRecording = async () => {
  try {
    stream.value = await navigator.mediaDevices.getUserMedia({audio: true})
    audioContext.value = new (window.AudioContext || window.webkitAudioContext)()
    processor.value = audioContext.value.createScriptProcessor(1024, 1, 1)

    input.value = audioContext.value.createMediaStreamSource(stream.value)
    input.value.connect(processor.value)
    processor.value.connect(audioContext.value.destination)

    socket.value = new WebSocket(SOCKET_URL)
    socket.value.binaryType = 'arraybuffer'
    const prefixAuthorization = 'Bearer ';

    socket.value.onopen = () => {
      socket.value.send(JSON.stringify({Authorization: prefixAuthorization + getToken()}))
    }

    processor.value.onaudioprocess = (event) => {
      const audioData = event.inputBuffer.getChannelData(0)
      const int16Array = convertFloat32ToInt16(audioData)
      socket.value.send(int16Array.buffer)
    }
  } catch (error) {
    console.error('Error accessing microphone:', error)
  }
}

const stopRecording = () => {
  if (audioContext.value && processor.value && stream.value) {
    processor.value.disconnect()
    input.value.disconnect()
    stream.value.getTracks().forEach(track => track.stop())
    audioContext.value.close()
  }
  if (socket.value) {
    socket.value.close()
    socket.value = null;
  }
}

const convertFloat32ToInt16 = (buffer) => {
  let l = buffer.length;
  const buf = new Int16Array(l);
  while (l--) {
    buf[l] = Math.min(1, buffer[l]) * 0x7FFF;
  }
  return buf;
}

onBeforeUnmount(() => {
  stopRecording()
})
</script>

<style scoped>
button {
  margin: 5px;
}
</style>
