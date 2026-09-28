import request from '../utils/request'

/**
 * 商品接口封装。
 */
export const productApi = {
  /** 分类列表 */
  listCategories() {
    return request.get('/product/category/list')
  },
  /** 商品分页列表（params: { categoryId, page, size }） */
  listProducts(params) {
    return request.get('/product/list', { params })
  },
  /** 商品详情 */
  getProduct(id) {
    return request.get(`/product/${id}`)
  },
}
