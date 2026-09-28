import request from '../utils/request'

/**
 * 系统接口封装。
 */
export const systemApi = {
  /** 健康检查 */
  ping() {
    return request.get('/system/ping')
  },
}
