<template>
  <div class="question-manage">
    <el-card>
      <template #header>
        <div class="head">
          <span>提问管理</span>
          <div class="tools">
            <el-select v-model="query.status" placeholder="状态" clearable style="width: 110px" @change="loadData">
              <el-option label="待回答" :value="1" />
              <el-option label="已回答" :value="2" />
            </el-select>
            <el-input v-model="query.keyword" placeholder="搜索问题" clearable style="width: 200px" @keyup.enter="loadData" @clear="loadData" />
            <el-button type="primary" @click="loadData">搜索</el-button>
          </div>
        </div>
      </template>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="userName" label="提问学员" width="110" />
        <el-table-column prop="courseName" label="课程" min-width="130" show-overflow-tooltip />
        <el-table-column prop="chapterName" label="章节" min-width="130" show-overflow-tooltip />
        <el-table-column prop="question" label="问题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="answer" label="回答" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">{{ row.answer || '未回答' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : 'warning'" size="small">
              {{ row.status === 2 ? '已回答' : '待回答' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提问时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain :disabled="row.status === 2" @click="openReply(row)">
              {{ row.status === 2 ? '已回复' : '回复' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="pager"
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog v-model="replyVisible" title="回复提问" width="520px" @closed="resetReply">
      <el-descriptions :column="1" border v-if="current">
        <el-descriptions-item label="提问学员">{{ current.userName }}</el-descriptions-item>
        <el-descriptions-item label="课程 / 章节">{{ current.courseName }} / {{ current.chapterName }}</el-descriptions-item>
        <el-descriptions-item label="问题">{{ current.question }}</el-descriptions-item>
      </el-descriptions>
      <el-input v-model="replyText" type="textarea" :rows="4" maxlength="250" placeholder="请输入回答内容" class="reply-input" />
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" :loading="replying" @click="submitReply">提交回答</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { getQuestionPage, replyQuestion } from '../../api/question'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: null, keyword: '' })

const replyVisible = ref(false)
const current = ref(null)
const replyText = ref('')
const replying = ref(false)

const formatTime = (t) => (t ? String(t).replace('T', ' ').slice(0, 19) : '')

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      status: query.status || undefined,
      keyword: query.keyword || undefined
    }
    const res = await getQuestionPage(params)
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const openReply = (row) => {
  current.value = row
  replyText.value = row.answer || ''
  replyVisible.value = true
}

const resetReply = () => {
  current.value = null
  replyText.value = ''
}

const submitReply = async () => {
  if (!replyText.value.trim()) {
    ElMessage.warning('请输入回答内容')
    return
  }
  replying.value = true
  try {
    await replyQuestion(current.value.qid, replyText.value.trim())
    ElMessage.success('回答成功')
    replyVisible.value = false
    loadData()
  } finally {
    replying.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tools {
  display: flex;
  gap: 8px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.reply-input {
  margin-top: 16px;
}
</style>
