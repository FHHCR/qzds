<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { useAppStore } from '../stores/app'

const router = useRouter()
const appStore = useAppStore()
const backendStatus = ref('检测中...')

onMounted(async () => {
  try {
    const data = await request.get('/system/ping')
    backendStatus.value = `${data.service} - ${data.status}（${data.time}）`
  } catch (e) {
    backendStatus.value = '后端未连接'
  }
  // 已登录则拉取当前用户
  if (appStore.token) {
    try {
      appStore.setUser(await request.get('/user/me'))
    } catch (e) {
      appStore.logout() // token 失效则清除
    }
  }
})

function logout() {
  appStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="home">
    <h1>{{ appStore.appName }} · 前端骨架</h1>
    <p>技术栈：Vue 3 + Vite + Pinia + Vue Router + Element Plus + Axios</p>
    <p>后端连通性：<span :class="{ ok: backendStatus.startsWith('mall') }">{{ backendStatus }}</span></p>

    <el-divider />

    <template v-if="appStore.token && appStore.user">
      <p>当前用户：{{ appStore.user.nickname }}（{{ appStore.user.username }}）</p>
      <el-button @click="logout">退出登录</el-button>
    </template>
    <el-button v-else type="primary" @click="router.push('/login')">去登录</el-button>
  </div>
</template>

<style scoped>
.home {
  max-width: 640px;
  margin: 80px auto;
  padding: 32px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  text-align: center;
}
.ok {
  color: #67c23a;
  font-weight: 600;
}
</style>
