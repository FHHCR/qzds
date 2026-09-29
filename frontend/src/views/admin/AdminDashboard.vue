<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { adminApi } from '../../api/admin'

const stats = ref({
  productCount: 0,
  categoryCount: 0,
  orderCount: 0,
  salesAmount: 0,
  trend: [],
})
const loading = ref(false)

let chart = null

function formatAmount(price) {
  return ((price || 0) / 100).toFixed(2)
}

function renderChart(trend) {
  if (!chart || !trend) return
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['订单数', '销售额（元）'] },
    grid: { left: 50, right: 60, top: 50, bottom: 30 },
    xAxis: {
      type: 'category',
      data: trend.map((t) => t.day.slice(5)),
    },
    yAxis: [
      { type: 'value', name: '订单数', minInterval: 1 },
      { type: 'value', name: '销售额（元）' },
    ],
    series: [
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        data: trend.map((t) => t.count),
        itemStyle: { color: '#e64340' },
      },
      {
        name: '销售额（元）',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: trend.map((t) => (t.amount / 100).toFixed(2)),
        itemStyle: { color: '#409eff' },
      },
    ],
  })
}

async function load() {
  loading.value = true
  try {
    stats.value = await adminApi.getStats()
    renderChart(stats.value.trend)
  } finally {
    loading.value = false
  }
}

function onResize() {
  chart && chart.resize()
}

onMounted(() => {
  chart = echarts.init(document.getElementById('trendChart'))
  load()
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  chart && chart.dispose()
})
</script>

<template>
  <div v-loading="loading">
    <div class="cards">
      <div class="card">
        <div class="label">商品总数</div>
        <div class="value">{{ stats.productCount }}</div>
      </div>   
      <div class="card">
        <div class="label">分类总数</div>
        <div class="value">{{ stats.categoryCount }}</div>
      </div>
      <div class="card">
        <div class="label">订单总数</div>
        <div class="value">{{ stats.orderCount }}</div>
      </div>
      <div class="card">
        <div class="label">销售总额（元）</div>
        <div class="value money">¥ {{ formatAmount(stats.salesAmount) }}</div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-title">近 7 日订单趋势</div>
      <div id="trendChart" class="chart"></div>
    </div>
  </div>
</template>

<style scoped>
.cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}
.card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}
.label {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}
.value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}
.money {
  color: #e64340;
}
.panel {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}
.panel-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 12px;
}
.chart {
  width: 100%;
  height: 360px;
}
</style>
