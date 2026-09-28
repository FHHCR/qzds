<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { orderApi } from '../api/order'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(true)

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
    order.value = await orderApi.getOrder(route.params.id)
  } catch (e) {
    order.value = null
  } finally {
    loading.value = false
  }
}

async function pay() {
  await orderApi.payOrder(order.value.id)
  ElMessage.success('支付成功')
  load()
}

async function cancel() {
  await orderApi.cancelOrder(order.value.id)
  ElMessage.success('订单已取消')
  load()
}

onMounted(load)
</script>

<template>
  <div v-loading="loading" class="detail">
    <template v-if="order">
      <div class="card">
        <div class="head">
          <div>
            <p class="no">订单号：{{ order.orderNo }}</p>
            <p class="time">{{ fmtTime(order.createTime) }}</p>
          </div>
          <el-tag :type="statusMap[order.status]?.type" size="large">
            {{ statusMap[order.status]?.text }}
          </el-tag>
        </div>

        <div v-for="item in order.items" :key="item.id" class="row">
          <div class="image">
            <img v-if="item.productImage" :src="item.productImage" :alt="item.productName" />
            <span v-else>暂无图片</span>
          </div>
          <div class="info">
            <p class="name">{{ item.productName }}</p>
            <p class="unit">单价 ¥ {{ formatPrice(item.price) }}</p>
          </div>
          <span class="qty">x{{ item.quantity }}</span>
          <span class="subtotal">¥ {{ formatPrice(item.price * item.quantity) }}</span>
        </div>

        <div class="footer">
          <span class="total">实付：<em>¥ {{ formatPrice(order.totalPrice) }}</em></span>
          <div class="actions">
            <template v-if="order.status === 0">
              <el-button type="primary" @click="pay">立即支付</el-button>
              <el-button @click="cancel">取消订单</el-button>
            </template>
            <el-button @click="router.push('/orders')">返回列表</el-button>
          </div>
        </div>
      </div>
    </template>
    <el-empty v-else-if="!loading" description="订单不存在" />
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
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}
.no {
  margin: 0 0 6px;
  font-weight: 600;
  font-size: 16px;
}
.time {
  margin: 0;
  color: #999;
  font-size: 13px;
}
.row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}
.image {
  width: 72px;
  height: 72px;
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
  width: 100px;
  text-align: right;
  font-weight: 600;
}
.footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
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
