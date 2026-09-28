import { defineStore } from 'pinia'

/** 应用级状态：登录态（token/user）与全局配置 */
export const useAppStore = defineStore('app', {
  state: () => ({
    appName: '电商平台',
    token: localStorage.getItem('token') || '',
    user: null,
  }),
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUser(user) {
      this.user = user
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
    },
  },
})
