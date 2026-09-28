<template>
  <el-card>
    <template #header>
      <div class="header">
        <span>课程管理</span>
        <div class="right">
          <el-input v-model="query.keyword" placeholder="课程名称" clearable style="width: 200px" @keyup.enter="loadData" @clear="loadData" />
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button type="success" @click="openDialog()">添加课程</el-button>
        </div>
      </div>
    </template>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="cuid" label="ID" width="70" />
      <el-table-column prop="courseName" label="课程名称" min-width="140" />
      <el-table-column label="分类" width="110">
        <template #default="{ row }">{{ categoryName(row.cid) }}</template>
      </el-table-column>
      <el-table-column prop="teacher" label="老师" width="90" />
      <el-table-column prop="learnTime" label="课时" width="70" />
      <el-table-column prop="clicked" label="点击" width="70" />
      <el-table-column label="推荐" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.recommend === 1" @change="(val) => handleRecommend(row, val)" />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '已删除' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/admin/courses/${row.cuid}/chapters`)">章节</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.cuid ? '编辑课程' : '添加课程'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程分类" prop="cid">
          <el-select v-model="form.cid" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in allCategories" :key="c.cid" :label="c.categoryName" :value="c.cid" />
          </el-select>
        </el-form-item>
        <el-form-item label="授课老师" prop="teacher">
          <el-input v-model="form.teacher" placeholder="请输入授课老师" />
        </el-form-item>
        <el-form-item label="课时">
          <el-input-number v-model="form.learnTime" :min="0" />
        </el-form-item>
        <el-form-item label="是否推荐">
          <el-switch v-model="form.recommend" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="封面图片">
          <div class="image-row">
            <el-input v-model="form.courseImage" placeholder="可直接粘贴图片 URL，或上传本地图片" />
            <el-upload :show-file-list="false" accept="image/*" :http-request="handleUploadImage" :disabled="uploading">
              <el-button size="small" type="primary" :loading="uploading">上传图片</el-button>
            </el-upload>
          </div>
          <div class="upload-hint">支持 jpg / png / gif / webp，最大 200MB；上传后自动填入地址</div>
        </el-form-item>
        <el-form-item label="课程介绍">
          <el-input v-model="form.courseExplain" type="textarea" :rows="3" placeholder="课程介绍（可留空）" />
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
import { getCoursePage, addCourse, updateCourse, deleteCourse, setCourseRecommend } from '../../api/course'
import { getAllCategories } from '../../api/category'
import { uploadImage } from '../../api/upload'

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const list = ref([])
const total = ref(0)
const allCategories = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const form = reactive({
  cuid: null,
  courseName: '',
  cid: null,
  teacher: '',
  learnTime: 0,
  courseImage: '',
  courseExplain: '',
  recommend: 0
})

const rules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  cid: [{ required: true, message: '请选择课程分类', trigger: 'change' }],
  teacher: [{ required: true, message: '请输入授课老师', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCoursePage({ pageNum: query.pageNum, pageSize: query.pageSize, keyword: query.keyword || undefined })
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

const categoryName = (cid) => {
  const c = allCategories.value.find((x) => x.cid === cid)
  return c ? c.categoryName : '-'
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, {
      cuid: row.cuid,
      courseName: row.courseName,
      cid: row.cid,
      teacher: row.teacher,
      learnTime: row.learnTime,
      courseImage: row.courseImage || '',
      courseExplain: row.courseExplain || '',
      recommend: row.recommend || 0
    })
  } else {
    Object.assign(form, {
      cuid: null,
      courseName: '',
      cid: null,
      teacher: '',
      learnTime: 0,
      courseImage: '',
      courseExplain: '',
      recommend: 0
    })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = {
      courseName: form.courseName,
      cid: form.cid,
      teacher: form.teacher,
      learnTime: form.learnTime || 0,
      courseImage: form.courseImage,
      courseExplain: form.courseExplain,
      recommend: form.recommend
    }
    if (form.cuid) {
      await updateCourse(form.cuid, payload)
    } else {
      await addCourse(payload)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleRecommend = async (row, val) => {
  await setCourseRecommend(row.cuid, val ? 1 : 0)
  row.recommend = val ? 1 : 0
  ElMessage.success(val ? '已设为推荐课程' : '已取消推荐')
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确认删除课程「${row.courseName}」？其章节将一并删除，可在回收站恢复。`, '提示', { type: 'warning' })
  await deleteCourse(row.cuid)
  ElMessage.success('删除成功')
  loadData()
}

const handleUploadImage = async (options) => {
  uploading.value = true
  try {
    const res = await uploadImage(options.file)
    form.courseImage = res.data
    ElMessage.success('图片上传成功')
  } finally {
    uploading.value = false
  }
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
.image-row {
  display: flex;
  gap: 8px;
  width: 100%;
}
.image-row .el-input {
  flex: 1;
}
.upload-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
