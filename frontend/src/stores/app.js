import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

/**
 * 应用级状态：登录态（token/user）与全局配置。
 * 组合式（Setup）风格：状态用 ref，派生值用 computed，方法直接声明。
 */
export const useAppStore = defineStore('app', () => {
  // 状态
  const appName = ref('电商平台')
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)

  // 派生状态
  const isLoggedIn = computed(() => !!token.value)

  // 方法
  function setToken(value) {
    token.value = value
    localStorage.setItem('token', value)
  }

  function setUser(value) {
    user.value = value
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  return { appName, token, user, isLoggedIn, setToken, setUser, logout }
})
