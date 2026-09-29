<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../api/admin'

const products = ref([])
const categories = ref([])
const total = ref(0)
const query = ref({ keyword: '', page: 1, size: 10 })
const loading = ref(false)

const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = reactive({ categoryId: null, name: '', mainImage: '', price: 0, stock: 0 })

const statusMap = {
  1: { text: '上架', type: 'success' },
  0: { text: '下架', type: 'info' },
}

async function load() {
  loading.value = true
  try {
    const data = await adminApi.listProducts(query.value)
    products.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  categories.value = await adminApi.listCategories()
}

function onSearch() {
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

function openAdd() {
  editingId.value = null
  form.categoryId = categories.value[0]?.id ?? null
  form.name = ''
  form.mainImage = ''
  form.price = 0
  form.stock = 0
  dialogVisible.value = true
}

function openEdit(p) {
  editingId.value = p.id
  form.categoryId = p.categoryId
  form.name = p.name
  form.mainImage = p.mainImage
  form.price = p.price / 100
  form.stock = p.stock
  dialogVisible.value = true
}

async function save() {
  if (!form.name || form.categoryId == null) {
    ElMessage.warning('请填写商品名称并选择分类')
    return
  }
  saving.value = true
  try {
    const payload = {
      categoryId: form.categoryId,
      name: form.name,
      mainImage: form.mainImage,
      price: Math.round(form.price * 100),
      stock: form.stock,
    }
    if (editingId.value) {
      await adminApi.updateProduct(editingId.value, payload)
      ElMessage.success('修改成功')
    } else {
      await adminApi.createProduct(payload)
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

async function toggleStatus(p) {
  await adminApi.updateProductStatus(p.id, p.status === 1 ? 0 : 1)
  ElMessage.success(p.status === 1 ? '已下架' : '已上架')
  load()
}

async function removeItem(p) {
  try {
    await ElMessageBox.confirm(`确定删除商品「${p.name}」吗？删除后前台不可见。`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await adminApi.deleteProduct(p.id)
  ElMessage.success('已删除')
  load()
}

onMounted(() => {
  loadCategories()
  load()
})
</script>

<template>
  <div>
    <div class="toolbar">
      <el-input
        v-model="query.keyword"
        class="search"
        placeholder="按商品名称搜索"
        clearable
        @keyup.enter="onSearch"
        @clear="onSearch"
      />
      <el-button type="primary" @click="openAdd">新增商品</el-button>
    </div>

    <el-table v-loading="loading" :data="products" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <el-image v-if="row.mainImage" :src="row.mainImage" fit="cover" class="thumb" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="180" />
      <el-table-column prop="categoryId" label="分类 ID" width="80" />
      <el-table-column label="价格" width="110">
        <template #default="{ row }">¥ {{ formatPrice(row.price) }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button link type="warning" size="small" @click="toggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button link type="danger" size="small" @click="removeItem(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑商品' : '新增商品'" width="520px">
      <el-form label-width="80px">
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="商品名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="主图 URL">
          <el-input v-model="form.mainImage" placeholder="https://..." maxlength="255" />
        </el-form-item>
        <el-form-item label="价格（元）">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
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
  gap: 12px;
  margin-bottom: 16px;
}
.search {
  width: 280px;
}
.thumb {
  width: 48px;
  height: 48px;
  border-radius: 4px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
