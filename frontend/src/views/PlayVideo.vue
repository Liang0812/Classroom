<template>
  <div class="play-page" v-loading="loading">
    <div class="main">
      <div class="player-wrap">
        <video
          v-if="info.chapter && info.chapter.videoUrl"
          :key="currentChid"
          :src="info.chapter.videoUrl"
          ref="videoRef"
          controls
          autoplay
          class="player"
          @loadedmetadata="handleLoadedMeta"
          @timeupdate="handleTimeUpdate"
          @ended="handleEnded"
        ></video>
        <el-empty v-else description="该章节暂无视频" />
      </div>
      <div class="player-tools">
        <div class="rates">
          <span class="tool-label">倍速</span>
          <el-radio-group v-model="rate" size="small" @change="applyRate">
            <el-radio-button v-for="r in rates" :key="r" :value="r">{{ r === 1 ? '1x' : r + 'x' }}</el-radio-button>
          </el-radio-group>
        </div>
        <div class="auto-next">
          <el-switch v-model="autoNext" size="small" />
          <span class="tool-label">自动连播下一章</span>
        </div>
      </div>
      <div class="chapter-nav">
        <div class="chapter-nav-title">章节列表（点击切换播放）</div>
        <div
          v-for="(ch, i) in info.chapters"
          :key="ch.chid"
          class="ch"
          :class="{ active: ch.chid === currentChid }"
          @click="switchChapter(ch)"
        >
          <span class="no">{{ i + 1 }}</span>
          <span class="name">{{ ch.chapterName }}</span>
        </div>
      </div>
    </div>
    <div class="side">
      <h3>{{ info.course ? info.course.courseName : '' }}</h3>
      <p class="teacher" v-if="info.course">授课老师：{{ info.course.teacher }}</p>
      <p class="teacher" v-if="info.course && info.course.courseExplain">
        {{ info.course.courseExplain }}
      </p>
      <el-divider content-position="left">本章提问</el-divider>
      <div class="qa-list">
        <div v-if="!questions.length" class="qa-empty">暂无提问，快来提出第一个问题吧</div>
        <div v-for="q in questions" :key="q.qid" class="qa-item">
          <div class="qa-q">
            <el-tag size="small" :type="q.status === 2 ? 'success' : 'warning'">
              {{ q.status === 2 ? '已回答' : '待回答' }}
            </el-tag>
            <span class="qa-question">{{ q.question }}</span>
          </div>
          <div class="qa-meta">{{ q.userName }} · {{ formatTime(q.createTime) }}</div>
          <div v-if="q.answer" class="qa-a"><b>老师回答：</b>{{ q.answer }}</div>
        </div>
      </div>
      <el-input
        v-model="questionText"
        type="textarea"
        :rows="2"
        maxlength="250"
        placeholder="向老师提问（如：这一讲的某个知识点不理解）"
        class="qa-input"
      ></el-input>
      <div class="qa-actions">
        <el-button type="primary" size="small" :loading="asking" @click="handleAsk">提问</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { getPlayInfo, startLearn, endLearn, savePosition } from '../api/learn'
import { getChapterQuestions, askQuestion } from '../api/question'

const route = useRoute()
const loading = ref(false)
const info = reactive({ chapter: null, course: null, chapters: [] })
const currentChid = ref(Number(route.params.chid))
const videoRef = ref(null)

const rates = [0.5, 0.75, 1, 1.25, 1.5, 2]
const rate = ref(1)
const autoNext = ref(true)

const questions = ref([])
const questionText = ref('')
const asking = ref(false)

let lastSavedAt = 0

const applyRate = () => {
  if (videoRef.value) videoRef.value.playbackRate = rate.value
}

const handleLoadedMeta = () => {
  const v = videoRef.value
  if (!v) return
  // 续播：上次保存的位置（留出片尾 5 秒避免播完即跳）
  const resume = Number(info.resumePosition || 0)
  if (resume > 0 && v.duration && resume < v.duration - 5) {
    v.currentTime = resume
  }
}

const handleTimeUpdate = () => {
  const v = videoRef.value
  if (!v || !v.duration) return
  const now = Date.now()
  // 节流：每 10 秒保存一次播放位置
  if (now - lastSavedAt < 10000) return
  lastSavedAt = now
  savePosition(currentChid.value, Math.round(v.currentTime)).catch(() => {})
}

const flushPosition = () => {
  const v = videoRef.value
  if (v && v.duration && v.currentTime > 0) {
    navigator.sendBeacon(
      '/api/learn/position',
      new Blob([JSON.stringify({ chid: currentChid.value, position: Math.round(v.currentTime) })], { type: 'application/json' })
    )
  }
}

const loadPlayInfo = async (chid) => {
  loading.value = true
  try {
    const res = await getPlayInfo(chid)
    info.chapter = res.data.chapter
    info.course = res.data.course
    info.chapters = res.data.chapters || []
  } finally {
    loading.value = false
  }
}

const loadQuestions = async (chid) => {
  try {
    const res = await getChapterQuestions(chid)
    questions.value = res.data || []
  } catch (e) {
    questions.value = []
  }
}

const formatTime = (t) => (t ? String(t).replace('T', ' ').slice(0, 19) : '')

const handleAsk = async () => {
  if (!questionText.value.trim()) {
    ElMessage.warning('请输入问题内容')
    return
  }
  asking.value = true
  try {
    await askQuestion(currentChid.value, questionText.value.trim())
    questionText.value = ''
    ElMessage.success('提问成功，等待老师回答')
    loadQuestions(currentChid.value)
  } finally {
    asking.value = false
  }
}

const handleEnded = () => {
  endLearn(currentChid.value).catch(() => {})
  if (!autoNext.value) return
  const chapters = info.chapters || []
  const idx = chapters.findIndex((ch) => ch.chid === currentChid.value)
  const next = chapters[idx + 1]
  if (next) {
    ElMessage.info(`自动连播：${next.chapterName}`)
    switchChapter(next)
  }
}

const switchChapter = async (ch) => {
  if (ch.chid === currentChid.value) return
  // 记录旧章节结束
  await endLearn(currentChid.value).catch(() => {})
  currentChid.value = ch.chid
  info.chapter = ch
  questionText.value = ''
  await startLearn(currentChid.value).catch(() => {})
  loadQuestions(currentChid.value)
}

onMounted(async () => {
  await loadPlayInfo(currentChid.value)
  await startLearn(currentChid.value).catch(() => {})
  loadQuestions(currentChid.value)
})

onBeforeUnmount(() => {
  // 离开页面时记录结束时间与播放位置（sendBeacon 保证请求发出）
  navigator.sendBeacon(`/api/learn/end?chid=${currentChid.value}`)
  flushPosition()
})
</script>

<style scoped>
.play-page {
  display: flex;
  gap: 16px;
}
.main {
  flex: 1;
  min-width: 0;
}
.player-wrap {
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}
.player {
  width: 100%;
  aspect-ratio: 16 / 9;
  display: block;
  background: #000;
}
.player-tools {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin: 10px 0;
}
.rates {
  display: flex;
  align-items: center;
  gap: 8px;
}
.auto-next {
  display: flex;
  align-items: center;
  gap: 8px;
}
.tool-label {
  font-size: 13px;
  color: #606266;
}
.chapter-nav {
  margin-top: 12px;
}
.chapter-nav-title {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}
.ch {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 12px;
  border: 1px solid #e6e6e6;
  border-radius: 6px;
  margin-bottom: 6px;
  cursor: pointer;
  transition: background 0.2s;
}
.ch:hover {
  background: #f5f7fa;
}
.ch.active {
  border-color: #409eff;
  background: #ecf5ff;
}
.no {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #c0c4cc;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  flex-shrink: 0;
}
.ch.active .no {
  background: #409eff;
}
.name {
  font-size: 14px;
}
.side {
  width: 300px;
  flex-shrink: 0;
}
.side h3 {
  margin: 0 0 8px;
}
.teacher {
  color: #606266;
  font-size: 13px;
  line-height: 1.8;
  margin: 0 0 8px;
}
.qa-list {
  max-height: 260px;
  overflow-y: auto;
  margin-bottom: 10px;
}
.qa-empty {
  color: #909399;
  font-size: 13px;
  padding: 12px 0;
}
.qa-item {
  border: 1px solid #e6e6e6;
  border-radius: 6px;
  padding: 8px 10px;
  margin-bottom: 8px;
}
.qa-q {
  display: flex;
  align-items: flex-start;
  gap: 6px;
}
.qa-question {
  font-size: 13px;
  line-height: 1.5;
}
.qa-meta {
  font-size: 12px;
  color: #909399;
  margin: 4px 0;
}
.qa-a {
  font-size: 13px;
  color: #35705a;
  background: #f0f9eb;
  border-radius: 4px;
  padding: 6px 8px;
  line-height: 1.5;
}
.qa-input {
  margin-bottom: 8px;
}
.qa-actions {
  text-align: right;
}
</style>
