import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

/**
 * 应用级状态：登录态（token/user/role）与全局配置。
 * 组合式（Setup）风格：状态用 ref，派生值用 computed，方法直接声明。
 */
export const useAppStore = defineStore('app', () => {
  // 状态
  const appName = ref('电商平台')
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const user = ref(null)

  // 派生状态
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === '2')

  // 方法
  function setToken(value) {
    token.value = value
    localStorage.setItem('token', value)
  }

  function setUser(value) {
    user.value = value
    if (value && value.role != null) {
      role.value = String(value.role)
      localStorage.setItem('role', role.value)
    }
  }

  function logout() {
    token.value = ''
    user.value = null
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('role')
  }

  return { appName, token, role, user, isLoggedIn, isAdmin, setToken, setUser, logout }
})
