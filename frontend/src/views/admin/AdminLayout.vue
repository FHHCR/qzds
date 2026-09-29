<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '../../stores/app'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

function logout() {
  appStore.logout()
  router.push('/login')
}
</script>

<template>
  <el-container class="admin">
    <el-aside width="200px" class="aside">
      <div class="brand">电商平台 · 管理端</div>
      <el-menu :default-active="route.path" router class="menu">
        <el-menu-item index="/admin/dashboard">仪表盘</el-menu-item>
        <el-menu-item index="/admin/products">商品管理</el-menu-item>
        <el-menu-item index="/admin/categories">分类管理</el-menu-item>
        <el-menu-item index="/admin/orders">订单管理</el-menu-item>
        <el-menu-item index="/admin/users">用户管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <el-button link @click="router.push('/')">返回前台</el-button>
        <span class="who">{{ appStore.user?.nickname || '管理员' }}</span>
        <el-button link type="danger" @click="logout">退出</el-button>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin {
  min-height: 100vh;
}
.aside {
  background: #001529;
  color: #fff;
}
.brand {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.menu {
  border-right: none;
  background: transparent;
}
.menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.75);
}
.menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08);
}
.menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: #e64340;
}
.topbar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.who {
  font-size: 14px;
  color: #666;
}
.main {
  background: #f5f7fa;
}
</style>
