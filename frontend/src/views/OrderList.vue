<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api/order'

const router = useRouter()
const orders = ref([])
const loading = ref(false)

const statusMap = {
  0: { text: '待支付', type: 'warning' },
  1: { text: '已支付', type: 'success' },
  2: { text: '已取消', type: 'info' },
}

function formatPrice(price) {
  return ((price || 0) / 100).toFixed(2)
}

function fmtTime(t) {
  return t ? t.replace('T', ' ').slice(0, 19) : ''
}

async function load() {
  loading.value = true
  try {
    orders.value = await orderApi.listOrders()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="orders">
    <h2>我的订单</h2>

    <div v-loading="loading" class="list">
      <el-card v-for="o in orders" :key="o.id" class="order" shadow="hover" @click="router.push(`/orders/${o.id}`)">
        <div class="head">
          <span class="no">订单号：{{ o.orderNo }}</span>
          <el-tag :type="statusMap[o.status]?.type" size="small">{{ statusMap[o.status]?.text }}</el-tag>
        </div>
        <div class="body">
          <span>共 {{ o.itemCount }} 件</span>
          <span class="time">{{ fmtTime(o.createTime) }}</span>
          <span class="price">¥ {{ formatPrice(o.totalPrice) }}</span>
        </div>
      </el-card>
      <el-empty v-if="!loading && orders.length === 0" description="暂无订单">
        <el-button type="primary" @click="router.push('/products')">去逛逛</el-button>
      </el-empty>
    </div>
  </div>
</template>

<style scoped>
.orders {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;
}
.list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.order {
  cursor: pointer;
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.no {
  font-weight: 600;
}
.body {
  display: flex;
  align-items: center;
  gap: 24px;
  color: #666;
  font-size: 13px;
}
.price {
  margin-left: auto;
  color: #e64340;
  font-size: 18px;
  font-weight: 700;
}
</style>
