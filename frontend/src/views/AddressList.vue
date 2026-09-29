<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addressApi } from '../api/address'

const router = useRouter()
const addresses = ref([])
const loading = ref(false)

const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = reactive({ receiver: '', phone: '', region: '', detail: '' })

async function load() {
  loading.value = true
  try {
    addresses.value = await addressApi.list()
  } finally {
    loading.value = false
  }
}

function openAdd() {
  editingId.value = null
  form.receiver = ''
  form.phone = ''
  form.region = ''
  form.detail = ''
  dialogVisible.value = true
}

function openEdit(a) {
  editingId.value = a.id
  form.receiver = a.receiver
  form.phone = a.phone
  form.region = a.region
  form.detail = a.detail
  dialogVisible.value = true
}

async function save() {
  if (!form.receiver || !form.phone || !form.detail) {
    ElMessage.warning('请填写收货人、手机号和详细地址')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await addressApi.update(editingId.value, form)
      ElMessage.success('修改成功')
    } else {
      await addressApi.add(form)
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

async function removeItem(a) {
  try {
    await ElMessageBox.confirm(`确定删除收货地址「${a.receiver}」吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await addressApi.delete(a.id)
  ElMessage.success('已删除')
  load()
}

async function setDefault(a) {
  await addressApi.setDefault(a.id)
  ElMessage.success('已设为默认')
  load()
}

onMounted(load)
</script>

<template>
  <div class="address">
    <div class="head">
      <h2>收货地址</h2>
      <el-button type="primary" @click="openAdd">新增地址</el-button>
    </div>

    <div v-loading="loading" class="list">
      <el-card v-for="a in addresses" :key="a.id" class="item" shadow="hover">
        <div class="line">
          <strong>{{ a.receiver }}</strong>
          <span>{{ a.phone }}</span>
          <el-tag v-if="a.isDefault === 1" size="small" type="primary">默认</el-tag>
        </div>
        <p class="detail">{{ a.region }} {{ a.detail }}</p>
        <div class="ops">
          <el-button v-if="a.isDefault !== 1" link type="primary" size="small" @click="setDefault(a)">设为默认</el-button>
          <el-button link type="primary" size="small" @click="openEdit(a)">编辑</el-button>
          <el-button link type="danger" size="small" @click="removeItem(a)">删除</el-button>
        </div>
      </el-card>
      <el-empty v-if="!loading && addresses.length === 0" description="暂无收货地址" />
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑地址' : '新增地址'" width="480px">
      <el-form label-width="80px">
        <el-form-item label="收货人">
          <el-input v-model="form.receiver" placeholder="收货人姓名" maxlength="50" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="手机号" maxlength="20" />
        </el-form-item>
        <el-form-item label="省市区">
          <el-input v-model="form.region" placeholder="如：浙江省 杭州市 余杭区" maxlength="100" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="form.detail" type="textarea" :rows="2" placeholder="街道、门牌号等" maxlength="255" />
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
.address {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
  margin-top: 16px;
}
.line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.detail {
  margin: 0 0 10px;
  color: #666;
  font-size: 13px;
}
.ops {
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #f5f5f5;
  padding-top: 10px;
}
</style>
