<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '../api/user'
import { useAppStore } from '../stores/app'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()

const mode = ref('login') // login | register
const form = ref({ username: '', password: '', nickname: '' })
const loading = ref(false)

async function submit() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    if (mode.value === 'login') {
      const data = await userApi.login({
        username: form.value.username,
        password: form.value.password,
      })
      appStore.setToken(data.token)
      appStore.setUser({ id: data.id, username: data.username, nickname: data.nickname })
      ElMessage.success('登录成功')
      router.push(route.query.redirect || '/')
    } else {
      await userApi.register(form.value)
      ElMessage.success('注册成功，请登录')
      mode.value = 'login'
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2>{{ mode === 'login' ? '登录' : '注册' }}</h2>
      <el-form label-position="top" @submit.prevent="submit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="3-20 位字母数字下划线" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="6-20 位" />
        </el-form-item>
        <el-form-item v-if="mode === 'register'" label="昵称（可选）">
          <el-input v-model="form.nickname" placeholder="昵称" />
        </el-form-item>
        <el-button type="primary" :loading="loading" style="width: 100%" @click="submit">
          {{ mode === 'login' ? '登录' : '注册' }}
        </el-button>
      </el-form>
      <p class="switch" @click="mode = mode === 'login' ? 'register' : 'login'">
        {{ mode === 'login' ? '没有账号？去注册' : '已有账号？去登录' }}
      </p>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}
.login-card {
  width: 380px;
}
h2 {
  margin: 0 0 16px;
  text-align: center;
}
.switch {
  text-align: center;
  color: #409eff;
  cursor: pointer;
  margin-top: 12px;
}
</style>
