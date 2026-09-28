<template>
  <div class="my-questions">
    <el-card>
      <template #header>我的提问</template>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="courseName" label="课程" min-width="140" />
        <el-table-column prop="chapterName" label="章节" min-width="140" />
        <el-table-column prop="question" label="问题" min-width="220" show-overflow-tooltip />
        <el-table-column prop="answer" label="老师回答" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">{{ row.answer || '等待回答…' }}</template>
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
      </el-table>
      <el-empty v-if="!loading && !list.length" description="暂无提问" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyQuestions } from '../api/question'

const loading = ref(false)
const list = ref([])

const formatTime = (t) => (t ? String(t).replace('T', ' ').slice(0, 19) : '')

onMounted(async () => {
  loading.value = true
  try {
    const res = await getMyQuestions()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
})
</script>
