<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi } from './api/user'
import { useAppStore } from './stores/app'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

onMounted(async () => {
  // 已登录但 store 无用户信息时（如刷新页面），拉取一次
  if (appStore.token && !appStore.user) {
    try {
      appStore.setUser(await userApi.getMe())
    } catch (e) {
      appStore.logout()
    }
  }
})

function logout() {
  appStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="app">
    <header v-if="route.name !== 'login' && !route.meta.admin" class="header">
      <span class="logo" @click="router.push('/')">电商平台</span>
      <nav class="nav">
        <router-link to="/">首页</router-link>
        <router-link to="/products">商品</router-link>
        <router-link to="/cart">购物车</router-link>
        <router-link to="/orders">我的订单</router-link>
        <router-link to="/address">收货地址</router-link>
        <router-link v-if="appStore.isAdmin" to="/admin/products">后台管理</router-link>
      </nav>
      <div class="user">
        <template v-if="appStore.token">
          <span class="nickname">{{ appStore.user?.nickname || '已登录' }}</span>
          <el-button link type="danger" @click="logout">退出</el-button>
        </template>
        <el-button v-else type="primary" size="small" @click="router.push('/login')">登录</el-button>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
  background: #f5f7fa;
}
.header {
  display: flex;
  align-items: center;
  height: 56px;
  padding: 0 24px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 10;
}
.logo {
  font-size: 18px;
  font-weight: 700;
  color: #e64340;
  cursor: pointer;
  margin-right: 32px;
}
.nav {
  display: flex;
  gap: 24px;
  flex: 1;
}
.nav a {
  color: #333;
  text-decoration: none;
  font-size: 14px;
}
.nav a.router-link-active {
  color: #e64340;
  font-weight: 600;
}
.user {
  display: flex;
  align-items: center;
  gap: 8px;
}
.nickname {
  font-size: 14px;
  color: #666;
}
.main {
  padding-bottom: 40px;
}
</style>
