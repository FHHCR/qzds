<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '../api/product'

const route = useRoute()
const router = useRouter()
const categories = ref([])
const products = ref([])
const total = ref(0)
const query = ref({ categoryId: null, keyword: '', sort: '', page: 1, size: 8 })
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

/** 分类 / 排序切换：回到第一页重新加载 */
function onFilterChange() {
  query.value.page = 1
  loadProducts()
}

/** 搜索：回车触发，回到第一页 */
function onSearch() {
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
  // 支持从首页/外部跳转：/products?categoryId=xx&keyword=xx
  const categoryId = route.query.categoryId
  const keyword = route.query.keyword
  if (categoryId) {
    query.value.categoryId = Number(categoryId)
  }
  if (keyword) {
    query.value.keyword = String(keyword)
  }
  loadCategories()
  loadProducts()
})
</script>

<template>
  <div class="products">
    <h2>商品列表</h2>

    <!-- 搜索与排序 -->
    <div class="toolbar">
      <el-input
        v-model="query.keyword"
        class="search"
        placeholder="搜索商品名称，回车确认"
        clearable
        @keyup.enter="onSearch"
        @clear="onSearch"
      />
      <el-select v-model="query.sort" class="sort" placeholder="排序" @change="onFilterChange">
        <el-option label="默认排序" value="" />
        <el-option label="价格从低到高" value="price_asc" />
        <el-option label="价格从高到低" value="price_desc" />
      </el-select>
    </div>

    <el-radio-group v-model="query.categoryId" class="category-bar" @change="onFilterChange">
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
      <el-empty v-if="!loading && products.length === 0" description="没有找到相关商品" />
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
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.search {
  width: 320px;
}
.sort {
  width: 160px;
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
