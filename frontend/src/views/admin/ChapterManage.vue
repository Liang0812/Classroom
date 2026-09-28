<template>
  <el-card>
    <template #header>
      <div class="header">
        <span>章节管理（课程 ID：{{ cuid }}）</span>
        <div class="right">
          <el-button @click="$router.push('/admin/courses')">返回课程列表</el-button>
          <el-button type="success" @click="openDialog()">添加章节</el-button>
        </div>
      </div>
    </template>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="chid" label="ID" width="60" />
      <el-table-column prop="chapterName" label="章节名称" min-width="140" />
      <el-table-column prop="summary" label="章节介绍" min-width="130" show-overflow-tooltip />
      <el-table-column prop="videoUrl" label="视频地址" min-width="140" show-overflow-tooltip />
      <el-table-column prop="orders" label="排序" width="60" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '已删除' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="330" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="success" @click="openFiles(row)">文件{{ row.fileCount ? `(${row.fileCount})` : '' }}</el-button>
          <el-button link type="warning" @click="openQuizzes(row)">小测{{ row.quizCount ? `(${row.quizCount})` : '' }}</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.chid ? '编辑章节' : '添加章节'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="章节名称" prop="chapterName">
          <el-input v-model="form.chapterName" placeholder="请输入章节名称" />
        </el-form-item>
        <el-form-item label="章节介绍">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="章节介绍（可留空）" />
        </el-form-item>
        <el-form-item label="视频地址" prop="videoUrl">
          <div class="video-row">
            <el-input v-model="form.videoUrl" placeholder="可直接粘贴视频 URL，或上传本地视频" />
            <el-upload :show-file-list="false" accept="video/*" :http-request="handleUploadVideo" :disabled="uploading">
              <el-button size="small" type="primary" :loading="uploading">上传视频</el-button>
            </el-upload>
          </div>
          <div class="upload-hint">支持 mp4 / webm / ogg / mov，最大 200MB；上传后自动填入地址</div>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.orders" :min="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="备注（可留空）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 章节课件管理 -->
    <el-dialog v-model="filesVisible" :title="`章节课件 · ${currentChapterName}`" width="640px">
      <div class="toolbar">
        <el-upload :show-file-list="false" :http-request="handleUploadFile" :disabled="uploading">
          <el-button type="primary" size="small" :loading="uploading">上传课件</el-button>
        </el-upload>
        <span class="hint">支持 pdf / word / ppt / excel / txt / zip 等</span>
      </div>
      <el-table :data="files" border stripe size="small" v-loading="filesLoading">
        <el-table-column prop="fileName" label="文件名" min-width="180" show-overflow-tooltip />
        <el-table-column label="大小" width="100">
          <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="orders" label="排序" width="70" />
        <el-table-column prop="createTime" label="上传时间" width="160" />
        <el-table-column label="操作" width="90">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleDeleteFile(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 章节小测管理 -->
    <el-dialog v-model="quizzesVisible" :title="`章节小测 · ${currentChapterName}`" width="860px">
      <div class="toolbar">
        <el-button type="primary" size="small" @click="openQuizForm()">添加题目</el-button>
        <span class="hint">每章可设多道单选题，学员在课程详情进入小测作答</span>
      </div>
      <el-table :data="quizzes" border stripe size="small" v-loading="quizzesLoading">
        <el-table-column prop="qid" label="ID" width="60" />
        <el-table-column prop="question" label="题目" min-width="180" show-overflow-tooltip />
        <el-table-column label="选项" min-width="220">
          <template #default="{ row }">
            <div class="opts">
              <span>A.{{ row.optionA }}</span>
              <span>B.{{ row.optionB }}</span>
              <span>C.{{ row.optionC }}</span>
              <span>D.{{ row.optionD }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="answer" label="答案" width="70" />
        <el-table-column prop="orders" label="排序" width="70" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="openQuizForm(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDeleteQuiz(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="quizFormVisible" :title="quizForm.qid ? '编辑题目' : '添加题目'" width="560px" append-to-body>
        <el-form ref="quizFormRef" :model="quizForm" :rules="quizRules" label-width="70px">
          <el-form-item label="题目" prop="question">
            <el-input v-model="quizForm.question" type="textarea" :rows="2" placeholder="请输入题目内容" />
          </el-form-item>
          <el-form-item label="选项A" prop="optionA">
            <el-input v-model="quizForm.optionA" placeholder="选项 A 内容" />
          </el-form-item>
          <el-form-item label="选项B" prop="optionB">
            <el-input v-model="quizForm.optionB" placeholder="选项 B 内容" />
          </el-form-item>
          <el-form-item label="选项C" prop="optionC">
            <el-input v-model="quizForm.optionC" placeholder="选项 C 内容" />
          </el-form-item>
          <el-form-item label="选项D" prop="optionD">
            <el-input v-model="quizForm.optionD" placeholder="选项 D 内容" />
          </el-form-item>
          <el-form-item label="答案" prop="answer">
            <el-select v-model="quizForm.answer" placeholder="请选择正确答案" style="width: 100%">
              <el-option label="A" value="A" />
              <el-option label="B" value="B" />
              <el-option label="C" value="C" />
              <el-option label="D" value="D" />
            </el-select>
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="quizForm.orders" :min="0" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="quizFormVisible = false">取消</el-button>
          <el-button type="primary" :loading="savingQuiz" @click="handleSaveQuiz">保存</el-button>
        </template>
      </el-dialog>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { ElMessageBox } from 'element-plus/es/components/message-box/index.mjs'
import { getChaptersByCourse, addChapter, updateChapter, deleteChapter } from '../../api/chapter'
import { uploadVideo, uploadFile } from '../../api/upload'
import { getAdminChapterFiles, addChapterFile, deleteChapterFile } from '../../api/chapterFile'
import { getAdminQuizzes, addAdminQuiz, updateAdminQuiz, deleteAdminQuiz } from '../../api/quiz'

const route = useRoute()
const cuid = route.params.cuid

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const form = reactive({
  chid: null,
  chapterName: '',
  summary: '',
  videoUrl: '',
  orders: 0,
  remark: ''
})

const rules = {
  chapterName: [{ required: true, message: '请输入章节名称', trigger: 'blur' }],
  videoUrl: [{ required: true, message: '请输入视频地址', trigger: 'blur' }]
}

// ===== 章节课件 =====
const filesVisible = ref(false)
const filesLoading = ref(false)
const files = ref([])
const currentChapterId = ref(null)
const currentChapterName = ref('')

// ===== 章节小测 =====
const quizzesVisible = ref(false)
const quizzesLoading = ref(false)
const quizzes = ref([])
const quizFormVisible = ref(false)
const savingQuiz = ref(false)
const quizFormRef = ref()

const quizForm = reactive({
  qid: null,
  question: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  answer: '',
  orders: 0
})

const quizRules = {
  question: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  optionA: [{ required: true, message: '请输入选项 A', trigger: 'blur' }],
  optionB: [{ required: true, message: '请输入选项 B', trigger: 'blur' }],
  optionC: [{ required: true, message: '请输入选项 C', trigger: 'blur' }],
  optionD: [{ required: true, message: '请输入选项 D', trigger: 'blur' }],
  answer: [{ required: true, message: '请选择正确答案', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getChaptersByCourse(cuid)
    list.value = res.data
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, {
      chid: row.chid,
      chapterName: row.chapterName,
      summary: row.summary || '',
      videoUrl: row.videoUrl,
      orders: row.orders,
      remark: row.remark || ''
    })
  } else {
    Object.assign(form, { chid: null, chapterName: '', summary: '', videoUrl: '', orders: 0, remark: '' })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = {
      cuid: Number(cuid),
      chapterName: form.chapterName,
      summary: form.summary,
      videoUrl: form.videoUrl,
      orders: form.orders || 0,
      remark: form.remark
    }
    if (form.chid) {
      await updateChapter(form.chid, payload)
    } else {
      await addChapter(payload)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确认删除章节「${row.chapterName}」？删除后可在回收站恢复。`, '提示', { type: 'warning' })
  await deleteChapter(row.chid)
  ElMessage.success('删除成功')
  loadData()
}

const handleUploadVideo = async (options) => {
  uploading.value = true
  try {
    const res = await uploadVideo(options.file)
    form.videoUrl = res.data
    ElMessage.success('视频上传成功')
  } finally {
    uploading.value = false
  }
}

// ===== 文件管理 =====
const openFiles = async (row) => {
  currentChapterId.value = row.chid
  currentChapterName.value = row.chapterName
  filesVisible.value = true
  await loadFiles()
}

const loadFiles = async () => {
  filesLoading.value = true
  try {
    const res = await getAdminChapterFiles(currentChapterId.value)
    files.value = res.data || []
  } finally {
    filesLoading.value = false
  }
}

const handleUploadFile = async (options) => {
  uploading.value = true
  try {
    const res = await uploadFile(options.file)
    const data = {
      fileName: options.file.name,
      fileUrl: res.data,
      fileSize: options.file.size,
      orders: 0
    }
    await addChapterFile(currentChapterId.value, data)
    ElMessage.success('上传成功')
    await loadFiles()
    loadData()
  } finally {
    uploading.value = false
  }
}

const handleDeleteFile = async (row) => {
  await ElMessageBox.confirm(`确认删除文件「${row.fileName}」？`, '提示', { type: 'warning' })
  await deleteChapterFile(row.fileId)
  ElMessage.success('删除成功')
  await loadFiles()
  loadData()
}

const formatSize = (s) => {
  if (!s) return ''
  if (s < 1024) return s + ' B'
  if (s < 1024 * 1024) return (s / 1024).toFixed(1) + ' KB'
  return (s / 1024 / 1024).toFixed(1) + ' MB'
}

// ===== 小测管理 =====
const openQuizzes = async (row) => {
  currentChapterId.value = row.chid
  currentChapterName.value = row.chapterName
  quizzesVisible.value = true
  await loadQuizzes()
}

const loadQuizzes = async () => {
  quizzesLoading.value = true
  try {
    const res = await getAdminQuizzes(currentChapterId.value)
    quizzes.value = res.data || []
  } finally {
    quizzesLoading.value = false
  }
}

const openQuizForm = (row) => {
  if (row) {
    Object.assign(quizForm, {
      qid: row.qid,
      question: row.question,
      optionA: row.optionA,
      optionB: row.optionB,
      optionC: row.optionC,
      optionD: row.optionD,
      answer: row.answer,
      orders: row.orders
    })
  } else {
    Object.assign(quizForm, { qid: null, question: '', optionA: '', optionB: '', optionC: '', optionD: '', answer: '', orders: 0 })
  }
  quizFormVisible.value = true
}

const handleSaveQuiz = async () => {
  await quizFormRef.value.validate()
  savingQuiz.value = true
  try {
    const payload = {
      question: quizForm.question,
      optionA: quizForm.optionA,
      optionB: quizForm.optionB,
      optionC: quizForm.optionC,
      optionD: quizForm.optionD,
      answer: quizForm.answer,
      orders: quizForm.orders || 0
    }
    if (quizForm.qid) {
      await updateAdminQuiz(quizForm.qid, payload)
    } else {
      await addAdminQuiz(currentChapterId.value, payload)
    }
    ElMessage.success('保存成功')
    quizFormVisible.value = false
    await loadQuizzes()
    loadData()
  } finally {
    savingQuiz.value = false
  }
}

const handleDeleteQuiz = async (row) => {
  await ElMessageBox.confirm(`确认删除题目「${row.question}」？`, '提示', { type: 'warning' })
  await deleteAdminQuiz(row.qid)
  ElMessage.success('删除成功')
  await loadQuizzes()
  loadData()
}

onMounted(loadData)
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
.video-row {
  display: flex;
  gap: 8px;
  width: 100%;
}
.video-row .el-input {
  flex: 1;
}
.upload-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.hint {
  font-size: 12px;
  color: #909399;
}
.opts {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 12px;
  color: #606266;
}
</style>
