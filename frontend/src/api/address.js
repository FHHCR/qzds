import request from '../utils/request'

/**
 * 收货地址接口封装（需登录）。
 */
export const addressApi = {
  /** 地址列表 */
  list() {
    return request.get('/address/list')
  },
  /** 新增地址 */
  add(data) {
    return request.post('/address', data)
  },
  /** 修改地址 */
  update(id, data) {
    return request.put(`/address/${id}`, data)
  },
  /** 删除地址 */
  delete(id) {
    return request.delete(`/address/${id}`)
  },
  /** 设为默认地址 */
  setDefault(id) {
    return request.put(`/address/${id}/default`)
  },
}
