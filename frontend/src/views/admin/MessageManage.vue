<template>
  <div class="message-manage">
    <el-card>
      <template #header>
        <div class="head">
          <span>消息管理</span>
          <div class="tools">
            <el-input v-model="query.keyword" placeholder="搜索标题/内容" clearable style="width: 200px" @keyup.enter="loadData" @clear="loadData" />
            <el-button type="primary" @click="loadData">搜索</el-button>
            <el-button type="success" @click="openSend">发送消息</el-button>
          </div>
        </div>
      </template>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="220" show-overflow-tooltip />
        <el-table-column label="接收范围" width="120" align="center">
          <template #default="{ row }">
            {{ row.receiverUid === 0 ? '全体学员' : '定向学员' }}
          </template>
        </el-table-column>
        <el-table-column prop="senderName" label="发送人" width="110" />
        <el-table-column label="发送时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
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

    <el-dialog v-model="sendVisible" title="发送消息" width="520px" @closed="resetSend">
      <el-form label-width="90px">
        <el-form-item label="接收对象">
          <el-radio-group v-model="sendForm.receiverType">
            <el-radio value="all">全体学员</el-radio>
            <el-radio value="specific">指定学员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="sendForm.receiverType === 'specific'" label="选择学员">
          <el-select v-model="sendForm.receiverUid" placeholder="请选择学员" filterable style="width: 100%">
            <el-option v-for="u in students" :key="u.uid" :label="u.userName" :value="u.uid" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="sendForm.title" maxlength="100" show-word-limit placeholder="请输入消息标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="sendForm.content" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="请输入消息内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendVisible = false">取消</el-button>
        <el-button type="primary" :loading="sending" @click="submitSend">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { getMessagePage, sendMessage } from '../../api/message'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const sendVisible = ref(false)
const sending = ref(false)
const sendForm = reactive({ receiverType: 'all', receiverUid: null, title: '', content: '' })
const students = ref([])

const formatTime = (t) => (t ? String(t).replace('T', ' ').slice(0, 19) : '')

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      keyword: query.keyword || undefined
    }
    const res = await getMessagePage(params)
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadStudents = async () => {
  try {
    const { getUsersByRole } = await import('../../api/auth')
    const res = await getUsersByRole(3)
    students.value = res.data || []
  } catch (e) {
    students.value = []
  }
}

const openSend = () => {
  sendVisible.value = true
  loadStudents()
}

const resetSend = () => {
  sendForm.receiverType = 'all'
  sendForm.receiverUid = null
  sendForm.title = ''
  sendForm.content = ''
}

const submitSend = async () => {
  if (!sendForm.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  if (!sendForm.content.trim()) {
    ElMessage.warning('请输入内容')
    return
  }
  sending.value = true
  try {
    await sendMessage({
      receiverType: sendForm.receiverType,
      receiverUid: sendForm.receiverUid,
      title: sendForm.title.trim(),
      content: sendForm.content.trim()
    })
    ElMessage.success('消息已发送')
    sendVisible.value = false
    loadData()
  } finally {
    sending.value = false
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
</style>
