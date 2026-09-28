<template>
  <el-card class="quiz-page" v-loading="loading">
    <template #header>
      <div class="quiz-head">
        <span>{{ chapterName ? `章节小测 · ${chapterName}` : '章节小测' }}</span>
        <div class="head-right">
          <el-button size="small" @click="$router.back()">返回</el-button>
        </div>
      </div>
    </template>

    <el-empty v-if="!loading && !quizzes.length" description="本章暂无小测题目" />

    <div v-else class="questions">
      <div v-for="(q, i) in quizzes" :key="q.qid" class="question-item" :class="{ answered: q.userAnswer }">
        <div class="q-head">
          <span class="q-no">第 {{ i + 1 }} 题</span>
          <el-tag v-if="q.userAnswer && !resultShown" size="small" type="success">已作答</el-tag>
        </div>
        <div class="q-text">{{ q.question }}</div>
        <el-radio-group v-model="answers[q.qid]" class="options">
          <el-radio v-for="opt in ['A', 'B', 'C', 'D']" :key="opt" :value="opt" class="option">
            {{ opt }}. {{ q['option' + opt] }}
          </el-radio>
        </el-radio-group>

        <!-- 提交后的正误反馈 -->
        <div v-if="resultShown" class="q-result" :class="detailMap[q.qid] && detailMap[q.qid].correct ? 'ok' : 'no'">
          <template v-if="detailMap[q.qid]">
            {{ detailMap[q.qid].correct ? '✓ 回答正确' : `✗ 回答错误，正确答案是 ${detailMap[q.qid].answer}` }}
          </template>
        </div>
      </div>
    </div>

    <div class="submit-bar" v-if="quizzes.length">
      <el-button v-if="!resultShown" type="primary" :loading="submitting" @click="handleSubmit">
        提交答卷
      </el-button>
      <template v-else>
        <el-tag size="large" :type="scoreText === '满分' ? 'success' : 'warning'">
          得分：{{ result.correct }} / {{ result.total }}
        </el-tag>
        <el-button @click="reset">重新作答</el-button>
      </template>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { getChapterQuiz, submitQuiz } from '../api/quiz'

const route = useRoute()
const router = useRouter()
const chid = route.params.chid
const chapterName = route.query.name || ''

const loading = ref(false)
const submitting = ref(false)
const quizzes = ref([])
const answers = reactive({})
const resultShown = ref(false)
const result = ref(null)
const detailMap = reactive({})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getChapterQuiz(chid)
    quizzes.value = res.data || []
    quizzes.value.forEach((q) => {
      if (q.userAnswer) answers[q.qid] = q.userAnswer
      else answers[q.qid] = ''
    })
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  const list = quizzes.value.map((q) => ({ qid: q.qid, answer: answers[q.qid] || '' }))
  if (!list.some((a) => a.answer)) {
    ElMessage.warning('请至少作答一题')
    return
  }
  submitting.value = true
  try {
    const res = await submitQuiz({ chid: Number(chid), answers: list })
    result.value = res.data
    Object.keys(detailMap).forEach((k) => delete detailMap[k])
    ;(res.data.details || []).forEach((d) => {
      detailMap[d.qid] = d
    })
    resultShown.value = true
  } finally {
    submitting.value = false
  }
}

const scoreText = () => (result.value && result.value.correct === result.value.total ? '满分' : '')

const reset = () => {
  Object.keys(answers).forEach((k) => (answers[k] = ''))
  resultShown.value = false
  result.value = null
}

onMounted(loadData)
</script>

<style scoped>
.quiz-page {
  max-width: 860px;
  margin: 0 auto;
}
.quiz-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.head-right {
  display: flex;
  gap: 8px;
}
.questions {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.question-item {
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  padding: 14px 16px;
}
.question-item.answered {
  border-color: #e1f3d8;
  background: #f7fcf5;
}
.q-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.q-no {
  font-size: 13px;
  color: #909399;
  font-weight: 600;
}
.q-text {
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 12px;
}
.options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.option {
  display: flex;
  height: auto;
}
.option :deep(.el-radio__label) {
  white-space: normal;
  line-height: 1.5;
}
.q-result {
  margin-top: 10px;
  font-size: 13px;
  padding: 8px 10px;
  border-radius: 6px;
}
.q-result.ok {
  color: #529b2e;
  background: #f0f9eb;
}
.q-result.no {
  color: #d63200;
  background: #fef0f0;
}
.submit-bar {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
