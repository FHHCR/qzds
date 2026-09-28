import request from '../utils/request'

/**
 * 订单接口封装（需登录）。
 */
export const orderApi = {
  /** 创建订单（data: { cartIds: [] }） */
  createOrder(data) {
    return request.post('/order', data)
  },
  /** 订单列表 */
  listOrders() {
    return request.get('/order/list')
  },
  /** 订单详情 */
  getOrder(id) {
    return request.get(`/order/${id}`)
  },
  /** 支付（模拟） */
  payOrder(id) {
    return request.put(`/order/${id}/pay`)
  },
  /** 取消订单 */
  cancelOrder(id) {
    return request.put(`/order/${id}/cancel`)
  },
}
