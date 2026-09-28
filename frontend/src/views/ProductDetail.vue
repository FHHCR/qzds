<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi } from '../api/product'
import { cartApi } from '../api/cart'
import { useAppStore } from '../stores/app'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const product = ref(null)
const categories = ref([])
const quantity = ref(1)
const loading = ref(true)

const categoryName = computed(() => {
  const c = categories.value.find((c) => c.id === product.value?.categoryId)
  return c ? c.name : ''
})

async function load() {
  loading.value = true
  quantity.value = 1
  try {
    // 详情与分类相互独立：详情 404 不影响分类展示，反之亦然
    try {
      product.value = await productApi.getProduct(route.params.id)
    } catch (e) {
      product.value = null
    }
    try {
      categories.value = await productApi.listCategories()
    } catch (e) {
      categories.value = []
    }
  } finally {
    loading.value = false
  }
}

// 路由参数变化时重新加载（复用同一详情页组件时）
watch(() => route.params.id, load)

onMounted(load)

function formatPrice(price) {
  return (price / 100).toFixed(2)
}

function addToCart() {
  if (!appStore.isLoggedIn) {
    ElMessage.warning('请先登录后再加入购物车')
    router.push('/login')
    return
  }
  cartApi
    .addToCart({ productId: product.value.id, quantity: quantity.value })
    .then(() => {
      ElMessage.success('已加入购物车')
    })
    .catch(() => {})
}
</script>

<template>
  <div v-loading="loading" class="detail">
    <template v-if="product">
      <div class="card">
        <div class="image">
          <img v-if="product.mainImage" :src="product.mainImage" :alt="product.name" />
          <span v-else>暂无图片</span>
        </div>
        <div class="info">
          <p v-if="categoryName" class="category">{{ categoryName }}</p>
          <h2>{{ product.name }}</h2>
          <p class="price">¥ {{ formatPrice(product.price) }}</p>
          <p class="stock">
            库存：{{ product.stock }} 件
            <el-tag v-if="product.stock === 0" type="danger" size="small">已售罄</el-tag>
          </p>
          <div class="buy">
            <span>数量：</span>
            <el-input-number v-model="quantity" :min="1" :max="Math.max(product.stock, 1)" />
            <el-button
              type="primary"
              size="large"
              :disabled="product.stock === 0"
              @click="addToCart"
            >
              加入购物车
            </el-button>
          </div>
        </div>
      </div>
    </template>
    <el-empty v-else-if="!loading" description="商品不存在或已下架" />
  </div>
</template>

<style scoped>
.detail {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;
  min-height: 300px;
}
.card {
  display: flex;
  gap: 32px;
  background: #fff;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.image {
  width: 420px;
  height: 420px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}
.image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.info {
  flex: 1;
}
.category {
  color: #999;
  font-size: 13px;
}
.info h2 {
  margin: 8px 0 16px;
  font-size: 24px;
}
.price {
  color: #e64340;
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 16px;
}
.stock {
  color: #666;
  margin: 0 0 24px;
}
.buy {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
