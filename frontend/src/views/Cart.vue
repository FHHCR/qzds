<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { cartApi } from '../api/cart'
import { orderApi } from '../api/order'

const router = useRouter()
const items = ref([])
const loading = ref(false)

const checkedItems = computed(() => items.value.filter((i) => i.checked))
const checkedTotal = computed(() =>
  checkedItems.value.reduce((sum, i) => sum + (i.price || 0) * i.quantity, 0)
)
const allChecked = computed(() => items.value.length > 0 && checkedItems.value.length === items.value.length)

async function load() {
  loading.value = true
  try {
    const list = await cartApi.listCart()
    items.value = list.map((i) => ({ ...i, checked: true }))
  } finally {
    loading.value = false
  }
}

function formatPrice(price) {
  return ((price || 0) / 100).toFixed(2)
}

function toggleAll(checked) {
  items.value.forEach((i) => (i.checked = checked))
}

async function changeQuantity(item, quantity) {
  if (!quantity || quantity < 1) return
  try {
    await cartApi.updateCartItem(item.id, quantity)
    item.quantity = quantity
  } catch (e) {
    load()
  }
}

async function removeItem(item) {
  await cartApi.deleteCartItem(item.id)
  items.value = items.value.filter((i) => i.id !== item.id)
  ElMessage.success('已删除')
}

async function checkout() {
  const ids = checkedItems.value.map((i) => i.id)
  if (ids.length === 0) {
    ElMessage.warning('请先勾选要结算的商品')
    return
  }
  try {
    const order = await orderApi.createOrder({ cartIds: ids })
    ElMessage.success('下单成功')
    router.push(`/orders/${order.id}`)
  } catch (e) {
    load()
  }
}

onMounted(load)
</script>

<template>
  <div class="cart">
    <h2>购物车</h2>

    <div v-loading="loading" class="list">
      <template v-if="items.length">
        <div v-for="item in items" :key="item.id" class="row">
          <el-checkbox :model-value="item.checked" @change="(v) => (item.checked = v)" />
          <div class="image">
            <img v-if="item.mainImage" :src="item.mainImage" :alt="item.name" />
            <span v-else>暂无图片</span>
          </div>
          <div class="info">
            <p class="name">{{ item.name || '商品已失效' }}</p>
            <p v-if="item.price != null" class="unit">单价 ¥ {{ formatPrice(item.price) }}</p>
            <p v-else class="unit invalid">商品不存在或已下架</p>
          </div>
          <div class="qty">
            <el-input-number
              :model-value="item.quantity"
              :min="1"
              :max="Math.max(item.stock, 1)"
              :disabled="item.price == null"
              size="small"
              @change="(v) => changeQuantity(item, v)"
            />
          </div>
          <div class="subtotal">¥ {{ formatPrice((item.price || 0) * item.quantity) }}</div>
          <el-button link type="danger" @click="removeItem(item)">删除</el-button>
        </div>

        <div class="footer">
          <div class="all">
            <el-checkbox
              :model-value="allChecked"
              :indeterminate="checkedItems.length > 0 && !allChecked"
              @change="toggleAll"
            >全选</el-checkbox>
          </div>
          <span class="total">
            已选 {{ checkedItems.length }} 件，合计：<em>¥ {{ formatPrice(checkedTotal) }}</em>
          </span>
          <el-button type="primary" size="large" :disabled="!checkedItems.length" @click="checkout">
            去结算
          </el-button>
        </div>
      </template>
      <el-empty v-else-if="!loading" description="购物车还是空的">
        <el-button type="primary" @click="router.push('/products')">去逛逛</el-button>
      </el-empty>
    </div>
  </div>
</template>

<style scoped>
.cart {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;
}
.list {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}
.row:last-child {
  border-bottom: none;
}
.image {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
}
.image img {
  max-width: 100%;
  max-height: 100%;
}
.info {
  flex: 1;
}
.name {
  margin: 0 0 6px;
  font-size: 15px;
}
.unit {
  margin: 0;
  color: #e64340;
  font-size: 13px;
}
.unit.invalid {
  color: #999;
}
.qty {
  width: 150px;
}
.subtotal {
  width: 90px;
  text-align: right;
  font-weight: 600;
}
.footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 20px;
  padding-top: 16px;
}
.all {
  margin-right: auto;
}
.total {
  font-size: 14px;
}
.total em {
  color: #e64340;
  font-style: normal;
  font-size: 20px;
  font-weight: 700;
}
</style>
