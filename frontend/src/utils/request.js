import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * axios 统一封装：
 * - baseURL 指向 /api，开发环境经 vite 代理到后端 8080
 * - 请求拦截：自动携带 JWT（后续认证模块启用）
 * - 响应拦截：按后端统一 Result{code, message, data} 解包
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 0) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    // 直接返回业务数据
    return res.data
  },
  (error) => {
    // 统一从后端 Result 中取提示信息（HTTP 状态码与业务码一致）
    const message = error.response?.data?.message || error.message || '网络异常'
    if (error.response && error.response.status === 401) {
      // 登录态失效：清除 token 并跳转登录页
      localStorage.removeItem('token')
      ElMessage.error('登录已失效，请重新登录')
      window.location.href = '/login'
    } else {
      ElMessage.error(message)
    }
    return Promise.reject(error)
  }
)

export default request
