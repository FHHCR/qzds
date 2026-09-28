<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '../api/product'

const route = useRoute()
const router = useRouter()
const categories = ref([])
const products = ref([])
const total = ref(0)
const query = ref({ categoryId: null, page: 1, size: 8 })
const loading = ref(false)

async function loadCategories() {
  categories.value = await productApi.listCategories()
}

async function loadProducts() {
  loading.value = true
  try {
    const data = await productApi.listProducts(query.value)
    products.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function selectCategory() {
  // v-model 已同步 query.categoryId，这里只需回到第一页并重新加载
  query.value.page = 1
  loadProducts()
}

function onPageChange(page) {
  query.value.page = page
  loadProducts()
}

function formatPrice(price) {
  return (price / 100).toFixed(2)
}

onMounted(() => {
  // 支持从首页分类入口跳转：/products?categoryId=xx
  const categoryId = route.query.categoryId
  if (categoryId) {
    query.value.categoryId = Number(categoryId)
  }
  loadCategories()
  loadProducts()
})
</script>

<template>
  <div class="products">
    <h2>商品列表</h2>

    <el-radio-group v-model="query.categoryId" class="category-bar" @change="selectCategory">
      <el-radio-button :value="null">全部</el-radio-button>
      <el-radio-button v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</el-radio-button>
    </el-radio-group>

    <div v-loading="loading" class="grid">
      <el-card v-for="p in products" :key="p.id" class="card" shadow="hover" @click="router.push(`/products/${p.id}`)">
        <div class="image">
          <img v-if="p.mainImage" :src="p.mainImage" :alt="p.name" />
          <span v-else>暂无图片</span>
        </div>
        <div class="name">{{ p.name }}</div>
        <div class="price">¥ {{ formatPrice(p.price) }}</div>
      </el-card>
      <el-empty v-if="!loading && products.length === 0" description="暂无商品" />
    </div>

    <el-pagination
      v-if="total > query.size"
      class="pager"
      layout="prev, pager, next"
      :total="total"
      :page-size="query.size"
      :current-page="query.page"
      @current-change="onPageChange"
    />
  </div>
</template>

<style scoped>
.products {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;
}
.category-bar {
  margin-bottom: 20px;
}
.grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  min-height: 200px;
}
.card {
  width: 220px;
  cursor: pointer;
}
.image {
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
  font-size: 14px;
}
.image img {
  max-width: 100%;
  max-height: 100%;
}
.name {
  margin: 8px 0;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.price {
  color: #e64340;
  font-weight: 600;
  font-size: 16px;
}
.pager {
  margin-top: 20px;
  justify-content: center;
}
</style>
