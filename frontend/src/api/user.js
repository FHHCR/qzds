import request from '../utils/request'

/**
 * 用户接口封装。
 */
export const userApi = {
  /** 注册 */
  register(data) {
    return request.post('/user/register', data)
  },
  /** 登录，返回 JWT 与用户信息 */
  login(data) {
    return request.post('/user/login', data)
  },
  /** 获取当前登录用户（需认证） */
  getMe() {
    return request.get('/user/me')
  },
}
