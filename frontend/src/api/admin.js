import request from '../utils/request'

/**
 * 后台管理接口封装（需管理员登录，后端 ROLE_ADMIN 校验）。
 */
export const adminApi = {
  // ===== 商品管理 =====
  /** 商品分页（含下架，params: { keyword, page, size }） */
  listProducts(params) {
    return request.get('/admin/product/list', { params })
  },
  createProduct(data) {
    return request.post('/admin/product', data)
  },
  updateProduct(id, data) {
    return request.put(`/admin/product/${id}`, data)
  },
  /** 上架/下架：status 1 上架 / 0 下架 */
  updateProductStatus(id, status) {
    return request.put(`/admin/product/${id}/status?status=${status}`)
  },
  deleteProduct(id) {
    return request.delete(`/admin/product/${id}`)
  },

  // ===== 分类管理 =====
  listCategories() {
    return request.get('/admin/category/list')
  },
  createCategory(data) {
    return request.post('/admin/category', data)
  },
  updateCategory(id, data) {
    return request.put(`/admin/category/${id}`, data)
  },
  deleteCategory(id) {
    return request.delete(`/admin/category/${id}`)
  },

  // ===== 用户管理 =====
  /** 用户分页（params: { keyword, page, size }） */
  listUsers(params) {
    return request.get('/admin/user/list', { params })
  },
  /** 启用/禁用：status 1 启用 / 0 禁用 */
  updateUserStatus(id, status) {
    return request.put(`/admin/user/${id}/status?status=${status}`)
  },
  deleteUser(id) {
    return request.delete(`/admin/user/${id}`)
  },

  // ===== 统计 =====
  /** 管理端总览统计 */
  getStats() {
    return request.get('/admin/stats')
  },

  // ===== 订单管理 =====
  /** 订单分页（params: { status, page, size }） */
  listOrders(params) {
    return request.get('/admin/order/list', { params })
  },
  getOrder(id) {
    return request.get(`/admin/order/${id}`)
  },
}
