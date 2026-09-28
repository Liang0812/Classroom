<template>
  <el-card>
    <template #header>
      <div class="header">
        <span>分类管理</span>
        <div class="right">
          <el-input v-model="query.keyword" placeholder="分类名称" clearable style="width: 200px" @keyup.enter="loadData" @clear="loadData" />
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button type="success" @click="openDialog()">添加分类</el-button>
        </div>
      </div>
    </template>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="cid" label="ID" width="70" />
      <el-table-column prop="categoryName" label="分类名称" min-width="140" />
      <el-table-column label="父分类" width="140">
        <template #default="{ row }">{{ parentName(row.parentId) }}</template>
      </el-table-column>
      <el-table-column prop="orders" label="排序" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '已删除' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pager"
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadData"
      @current-change="loadData"
    />

    <el-dialog v-model="dialogVisible" :title="form.cid ? '编辑分类' : '添加分类'" width="420px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="form.parentId" placeholder="无（顶级分类）" clearable style="width: 100%">
            <el-option v-for="c in allCategories" :key="c.cid" :label="c.categoryName" :value="c.cid" :disabled="c.cid === form.cid" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.orders" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { ElMessageBox } from 'element-plus/es/components/message-box/index.mjs'
import { getCategoryPage, getAllCategories, addCategory, updateCategory, deleteCategory } from '../../api/category'

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const total = ref(0)
const allCategories = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const form = reactive({ cid: null, categoryName: '', parentId: null, orders: 0 })

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCategoryPage({ pageNum: query.pageNum, pageSize: query.pageSize, keyword: query.keyword || undefined })
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadAll = async () => {
  const res = await getAllCategories()
  allCategories.value = res.data
}

const parentName = (pid) => {
  const c = allCategories.value.find((x) => x.cid === pid)
  return c ? c.categoryName : '顶级'
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, { cid: row.cid, categoryName: row.categoryName, parentId: row.parentId === 0 ? null : row.parentId, orders: row.orders })
  } else {
    Object.assign(form, { cid: null, categoryName: '', parentId: null, orders: 0 })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = { categoryName: form.categoryName, parentId: form.parentId || 0, orders: form.orders || 0 }
    if (form.cid) {
      await updateCategory(form.cid, payload)
    } else {
      await addCategory(payload)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确认删除分类「${row.categoryName}」？删除后可在回收站恢复。`, '提示', { type: 'warning' })
  await deleteCategory(row.cid)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
  loadAll()
})
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.right {
  display: flex;
  gap: 8px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
