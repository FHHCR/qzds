<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '../api/product'

const router = useRouter()
const categories = ref([])
const products = ref([])
const loading = ref(false)
const keyword = ref('')

async function loadCategories() {
  categories.value = await productApi.listCategories()
}

async function loadProducts() {
  loading.value = true
  try {
    const data = await productApi.listProducts({ page: 1, size: 8 })
    products.value = data.records
  } finally {
    loading.value = false
  }
}

function formatPrice(price) {
  return (price / 100).toFixed(2)
}

function goCategory(categoryId) {
  router.push({ path: '/products', query: { categoryId } })
}

function onSearch() {
  const kw = keyword.value.trim()
  if (!kw) return
  router.push({ path: '/products', query: { keyword: kw } })
}

onMounted(() => {
  loadCategories()
  loadProducts()
})
</script>

<template>
  <div class="home">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="keyword"
        class="search"
        size="large"
        placeholder="搜索商品，如：手机、车厘子"
        clearable
        @keyup.enter="onSearch"
      >
        <template #append>
          <el-button type="primary" @click="onSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- Banner 轮播 -->
    <el-carousel height="280px" class="banner">
      <el-carousel-item v-for="n in 3" :key="n">
        <div class="banner-item">
          <img :src="`https://picsum.photos/seed/banner${n}/1200/560`" alt="banner" />
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 分类快捷入口 -->
    <section class="section">
      <h3>商品分类</h3>
      <div class="cats">
        <div v-for="c in categories" :key="c.id" class="cat" @click="goCategory(c.id)">
          {{ c.name }}
        </div>
      </div>
    </section>

    <!-- 推荐商品 -->
    <section class="section">
      <h3>为你推荐</h3>
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
      <el-button class="more" @click="router.push('/products')">查看全部商品</el-button>
    </section>
  </div>
</template>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.banner {
  border-radius: 8px;
  overflow: hidden;
}
.search-bar {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}
.search {
  width: 480px;
}
.banner-item {
  width: 100%;
  height: 100%;
}
.banner-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.section {
  margin-top: 28px;
}
.section h3 {
  margin: 0 0 16px;
  font-size: 18px;
}
.cats {
  display: flex;
  gap: 16px;
}
.cat {
  flex: 1;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s;
}
.cat:hover {
  color: #e64340;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
.more {
  margin-top: 16px;
  width: 100%;
}
</style>
