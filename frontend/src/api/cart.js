import request from '../utils/request'

/**
 * 购物车接口封装（需登录）。
 */
export const cartApi = {
  /** 加入购物车 */
  addToCart(data) {
    return request.post('/cart', data)
  },
  /** 购物车列表 */
  listCart() {
    return request.get('/cart/list')
  },
  /** 修改数量 */
  updateCartItem(id, quantity) {
    return request.put(`/cart/${id}`, { quantity })
  },
  /** 删除条目 */
  deleteCartItem(id) {
    return request.delete(`/cart/${id}`)
  },
}
