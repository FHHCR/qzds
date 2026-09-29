<script setup>
import { onMounted, ref } from 'vue'
import { adminApi } from '../../api/admin'

const orders = ref([])
const total = ref(0)
const query = ref({ status: null, page: 1, size: 10 })
const loading = ref(false)

const detailVisible = ref(false)
const detail = ref(null)

const statusMap = {
  0: { text: '待支付', type: 'warning' },
  1: { text: '已支付', type: 'success' },
  2: { text: '已取消', type: 'info' },
}

async function load() {
  loading.value = true
  try {
    const data = await adminApi.listOrders(query.value)
    orders.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function onFilterChange() {
  query.value.page = 1
  load()
}

function onPageChange(page) {
  query.value.page = page
  load()
}

function formatPrice(price) {
  return ((price || 0) / 100).toFixed(2)
}

function fmtTime(t) {
  return t ? t.replace('T', ' ').slice(0, 19) : ''
}

async function showDetail(row) {
  detail.value = await adminApi.getOrder(row.id)
  detailVisible.value = true
}

onMounted(load)
</script>

<template>
  <div>
    <div class="toolbar">
      <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 160px" @change="onFilterChange">
        <el-option label="待支付" :value="0" />
        <el-option label="已支付" :value="1" />
        <el-option label="已取消" :value="2" />
      </el-select>
    </div>

    <el-table v-loading="loading" :data="orders" border stripe>
      <el-table-column label="订单号" min-width="190">
        <template #default="{ row }">{{ row.orderNo }}</template>
      </el-table-column>
      <el-table-column prop="userId" label="用户 ID" width="80" />
      <el-table-column label="金额" width="110">
        <template #default="{ row }">¥ {{ formatPrice(row.totalPrice) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="itemCount" label="件数" width="70" />
      <el-table-column label="下单时间" width="170">
        <template #default="{ row }">{{ fmtTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="showDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > query.size"
      class="pager"
      layout="prev, pager, next"
      :total="total"
      :page-size="query.size"
      :current-page="query.page"
      @current-change="onPageChange"
    />

    <el-dialog v-model="detailVisible" title="订单详情" width="560px">
      <template v-if="detail">
        <p class="line">订单号：{{ detail.orderNo }}</p>
        <p class="line">状态：
          <el-tag :type="statusMap[detail.status]?.type" size="small">{{ statusMap[detail.status]?.text }}</el-tag>
        </p>
        <p class="line">下单时间：{{ fmtTime(detail.createTime) }}</p>
        <p class="line">收货地址：{{ detail.addressSnapshot }}</p>
        <el-table :data="detail.items" border size="small">
          <el-table-column prop="productName" label="商品" min-width="160" />
          <el-table-column label="单价" width="100">
            <template #default="{ row }">¥ {{ formatPrice(row.price) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="70" />
        </el-table>
        <p class="total">实付：¥ {{ formatPrice(detail.totalPrice) }}</p>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.line {
  margin: 0 0 8px;
  font-size: 14px;
}
.total {
  margin: 14px 0 0;
  text-align: right;
  font-weight: 700;
  color: #e64340;
  font-size: 16px;
}
</style>
