import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页' },
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/products',
    name: 'products',
    component: () => import('../views/ProductList.vue'),
    meta: { title: '商品列表', public: true },
  },
  {
    path: '/products/:id',
    name: 'product-detail',
    component: () => import('../views/ProductDetail.vue'),
    meta: { title: '商品详情', public: true },
  },
  {
    path: '/cart',
    name: 'cart',
    component: () => import('../views/Cart.vue'),
    meta: { title: '购物车' },
  },
  {
    path: '/checkout',
    name: 'checkout',
    component: () => import('../views/Checkout.vue'),
    meta: { title: '确认订单' },
  },
  {
    path: '/address',
    name: 'address',
    component: () => import('../views/AddressList.vue'),
    meta: { title: '收货地址' },
  },
  {
    path: '/orders',
    name: 'orders',
    component: () => import('../views/OrderList.vue'),
    meta: { title: '我的订单' },
  },
  {
    path: '/orders/:id',
    name: 'order-detail',
    component: () => import('../views/OrderDetail.vue'),
    meta: { title: '订单详情' },
  },
  // 后台管理（需管理员）
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { title: '后台管理', admin: true },
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'admin-dashboard',
        component: () => import('../views/admin/AdminDashboard.vue'),
        meta: { title: '仪表盘', admin: true },
      },
      {
        path: 'products',
        name: 'admin-products',
        component: () => import('../views/admin/AdminProducts.vue'),
        meta: { title: '商品管理', admin: true },
      },
      {
        path: 'categories',
        name: 'admin-categories',
        component: () => import('../views/admin/AdminCategories.vue'),
        meta: { title: '分类管理', admin: true },
      },
      {
        path: 'orders',
        name: 'admin-orders',
        component: () => import('../views/admin/AdminOrders.vue'),
        meta: { title: '订单管理', admin: true },
      },
      {
        path: 'users',
        name: 'admin-users',
        component: () => import('../views/admin/AdminUsers.vue'),
        meta: { title: '用户管理', admin: true },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 登录守卫：未登录访问非公开页 → 跳转登录页；非管理员访问管理端 → 回首页
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (!to.meta.public && !token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.admin && localStorage.getItem('role') !== '2') {
    return { path: '/' }
  }
})

router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} - 电商平台` : '电商平台'
})

export default router
