<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { cartApi } from '../api/cart'
import { addressApi } from '../api/address'
import { orderApi } from '../api/order'

const route = useRoute()
const router = useRouter()

const addresses = ref([])
const checkedIds = computed(() =>
  String(route.query.ids || '')
    .split(',')
    .map((s) => Number(s))
    .filter((n) => n > 0)
)
const selectedAddressId = ref(null)
const items = ref([])
const loading = ref(true)
const submitting = ref(false)

const totalPrice = computed(() =>
  items.value.reduce((sum, i) => sum + (i.price || 0) * i.quantity, 0)
)

function formatPrice(price) {
  return ((price || 0) / 100).toFixed(2)
}

async function load() {
  loading.value = true
  try {
    const [addrList, cartList] = await Promise.all([
      addressApi.list(),
      cartApi.listCart(),
    ])
    addresses.value = addrList
    // 默认选中默认地址，否则选第一条
    selectedAddressId.value =
      addrList.find((a) => a.isDefault === 1)?.id ?? addrList[0]?.id ?? null
    const idSet = new Set(checkedIds.value)
    items.value = cartList.filter((i) => idSet.has(i.id))
    if (items.value.length === 0) {
      ElMessage.warning('结算商品已失效，请返回购物车重新勾选')
    }
  } finally {
    loading.value = false
  }
}

async function submit() {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    const order = await orderApi.createOrder({
      cartIds: items.value.map((i) => i.id),
      addressId: selectedAddressId.value,
    })
    ElMessage.success('下单成功')
    router.push(`/orders/${order.id}`)
  } catch (e) {
    // 失败时刷新购物车（如商品已下架/库存不足），回到购物车重新勾选
    router.replace('/cart')
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<template>
  <div v-loading="loading" class="checkout">
    <h2>确认订单</h2>

    <template v-if="!loading">
      <!-- 收货地址 -->
      <div class="card">
        <div class="card-title">
          <span>收货地址</span>
          <el-button link type="primary" @click="router.push('/address')">管理地址</el-button>
        </div>
        <div v-if="addresses.length" class="addresses">
          <div
            v-for="a in addresses"
            :key="a.id"
            class="address"
            :class="{ active: a.id === selectedAddressId }"
            @click="selectedAddressId = a.id"
          >
            <div class="line">
              <strong>{{ a.receiver }}</strong>
              <span>{{ a.phone }}</span>
              <el-tag v-if="a.isDefault === 1" size="small" type="primary">默认</el-tag>
            </div>
            <p class="detail">{{ a.region }} {{ a.detail }}</p>
          </div>
        </div>
        <el-empty v-else description="还没有收货地址">
          <el-button type="primary" @click="router.push('/address')">去新增</el-button>
        </el-empty>
      </div>

      <!-- 商品清单 -->
      <div class="card">
        <div class="card-title"><span>商品清单</span></div>
        <div v-for="item in items" :key="item.id" class="row">
          <div class="image">
            <img v-if="item.mainImage" :src="item.mainImage" :alt="item.name" />
            <span v-else>暂无图片</span>
          </div>
          <div class="info">
            <p class="name">{{ item.name || '商品已失效' }}</p>
            <p class="unit">单价 ¥ {{ formatPrice(item.price) }}</p>
          </div>
          <span class="qty">x{{ item.quantity }}</span>
          <span class="subtotal">¥ {{ formatPrice((item.price || 0) * item.quantity) }}</span>
        </div>
      </div>

      <!-- 提交 -->
      <div class="footer">
        <span class="total">合计：<em>¥ {{ formatPrice(totalPrice) }}</em></span>
        <el-button
          type="primary"
          size="large"
          :loading="submitting"
          :disabled="!items.length || !addresses.length"
          @click="submit"
        >提交订单</el-button>
      </div>
    </template>
  </div>
</template>

<style scoped>
.checkout {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  margin-bottom: 14px;
}
.addresses {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.address {
  width: 280px;
  padding: 14px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.address.active {
  border-color: #409eff;
  background: #ecf5ff;
}
.line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.detail {
  margin: 0;
  color: #666;
  font-size: 13px;
}
.row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}
.row:last-child {
  border-bottom: none;
}
.image {
  width: 64px;
  height: 64px;
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
  margin: 0 0 4px;
}
.unit {
  margin: 0;
  color: #999;
  font-size: 13px;
}
.qty {
  color: #666;
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
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.total {
  font-size: 14px;
}
.total em {
  color: #e64340;
  font-style: normal;
  font-size: 22px;
  font-weight: 700;
}
</style>
