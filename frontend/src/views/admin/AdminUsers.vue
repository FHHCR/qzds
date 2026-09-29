<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../api/admin'
import { useAppStore } from '../../stores/app'

const appStore = useAppStore()

const users = ref([])
const total = ref(0)
const query = ref({ keyword: '', page: 1, size: 10 })
const loading = ref(false)

const roleMap = {
  1: { text: '普通用户', type: 'info' },
  2: { text: '管理员', type: 'danger' },
}
const statusMap = {
  1: { text: '正常', type: 'success' },
  0: { text: '禁用', type: 'danger' },
}

async function load() {
  loading.value = true
  try {
    const data = await adminApi.listUsers(query.value)
    users.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function onSearch() {
  query.value.page = 1
  load()
}

function onPageChange(page) {
  query.value.page = page
  load()
}

function fmtTime(t) {
  return t ? t.replace('T', ' ').slice(0, 19) : ''
}

/** 是否当前登录管理员自己（后端同样拦截，前端仅防呆隐藏操作） */
function isSelf(row) {
  return appStore.user?.id != null && String(row.id) === String(appStore.user.id)
}

/** 管理员账号不允许被操作 */
function isProtected(row) {
  return row.role === 2
}

async function toggleStatus(row) {
  await adminApi.updateUserStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success(row.status === 1 ? '已禁用' : '已启用')
  load()
}

async function removeItem(row) {
  try {
    await ElMessageBox.confirm(`确定删除用户「${row.username}」吗？删除后不可恢复。`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  try {
    await adminApi.deleteUser(row.id)
    ElMessage.success('已删除')
    load()
  } catch (e) {
    /* 不能删除自己/管理员时后端返回 400，提示已由拦截器处理 */
  }
}

onMounted(load)
</script>

<template>
  <div>
    <div class="toolbar">
      <el-input
        v-model="query.keyword"
        class="search"
        placeholder="按用户名 / 昵称搜索"
        clearable
        @keyup.enter="onSearch"
        @clear="onSearch"
      />
      <el-button type="primary" @click="onSearch">搜索</el-button>
    </div>

    <el-table v-loading="loading" :data="users" border stripe>
      <el-table-column label="ID" width="90">
        <template #default="{ row }">{{ row.id }}</template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" min-width="120" />
      <el-table-column prop="nickname" label="昵称" min-width="120" />
      <el-table-column label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="roleMap[row.role]?.type" size="small">{{ roleMap[row.role]?.text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" width="170">
        <template #default="{ row }">{{ fmtTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <template v-if="!isSelf(row) && !isProtected(row)">
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" size="small" @click="removeItem(row)">删除</el-button>
          </template>
          <span v-else class="na">-</span>
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
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.search {
  width: 280px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.na {
  color: #ccc;
}
</style>
