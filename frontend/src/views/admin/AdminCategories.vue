<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../api/admin'

const categories = ref([])
const loading = ref(false)

const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = reactive({ name: '', sort: 0 })

async function load() {
  loading.value = true
  try {
    categories.value = await adminApi.listCategories()
  } finally {
    loading.value = false
  }
}

function openAdd() {
  editingId.value = null
  form.name = ''
  form.sort = 0
  dialogVisible.value = true
}

function openEdit(c) {
  editingId.value = c.id
  form.name = c.name
  form.sort = c.sort
  dialogVisible.value = true
}

async function save() {
  if (!form.name) {
    ElMessage.warning('请填写分类名称')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await adminApi.updateCategory(editingId.value, form)
      ElMessage.success('修改成功')
    } else {
      await adminApi.createCategory(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } catch (e) {
    /* 错误提示已由拦截器处理 */
  } finally {
    saving.value = false
  }
}

async function removeItem(c) {
  try {
    await ElMessageBox.confirm(`确定删除分类「${c.name}」吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  try {
    await adminApi.deleteCategory(c.id)
    ElMessage.success('已删除')
    load()
  } catch (e) {
    /* 分类下有商品时后端返回 400，提示已由拦截器处理 */
  }
}

onMounted(load)
</script>

<template>
  <div>
    <div class="toolbar">
      <span class="tip">删除分类时，分类下存在商品将被拒绝</span>
      <el-button type="primary" style="margin-left: auto" @click="openAdd">新增分类</el-button>
    </div>

    <el-table v-loading="loading" :data="categories" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="分类名称" min-width="180" />
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="removeItem(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新增分类'" width="420px">
      <el-form label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="分类名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.tip {
  font-size: 13px;
  color: #999;
}
</style>
