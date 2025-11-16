<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  console.log('应用已启动')
})
</script>

<style scoped>
#app {
  width: 100%;
  height: 100vh;
}
</style>
