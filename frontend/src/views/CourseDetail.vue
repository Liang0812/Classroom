<template>
  <div class="course-detail" v-loading="loading">
    <template v-if="detail">
      <el-card class="info-card">
        <div class="top">
          <div class="cover">
            <img v-if="detail.course.courseImage" :src="detail.course.courseImage" :alt="detail.course.courseName" />
            <div v-else class="cover-placeholder">{{ detail.course.courseName }}</div>
          </div>
          <div class="main">
            <h2>{{ detail.course.courseName }}</h2>
            <div class="tags">
              <el-tag size="small">{{ categoryName(detail.course.cid) }}</el-tag>
              <el-tag size="small" type="warning" v-if="detail.course.recommend === 1">推荐</el-tag>
            </div>
            <div class="meta-line">授课老师：{{ detail.course.teacher }}</div>
            <div class="meta-line">课时：{{ detail.course.learnTime }} 小时 · 章节：{{ detail.chapters.length }} 个 · 学习时长：{{ detail.course.clicked }}</div>
            <div class="meta-line" v-if="detail.course.createTime">创建时间：{{ formatTime(detail.course.createTime) }}</div>
            <div class="actions">
              <el-button v-if="isLogin" :type="detail.collected ? 'warning' : 'primary'" plain @click="handleCollect">
                {{ detail.collected ? '取消收藏' : '收藏课程' }}
              </el-button>
              <el-button v-else type="primary" plain @click="$router.push('/login')">登录后收藏</el-button>
            </div>
          </div>
        </div>
        <el-divider content-position="left">课程介绍</el-divider>
        <p class="explain">{{ detail.course.courseExplain || '暂无介绍' }}</p>
        <div class="rating-summary">
          <el-rate :model-value="detail.avgRating || 0" disabled allow-half class="rate-stars" />
          <span class="rate-text">{{ detail.ratingCount ? detail.avgRating + ' 分 · ' + detail.ratingCount + ' 条评价' : '暂无评价' }}</span>
        </div>
      </el-card>

      <el-card class="chapter-card">
        <template #header>章节目录</template>
        <el-empty v-if="!detail.chapters.length" description="暂无章节" />
        <div v-else class="chapters">
          <div v-for="(ch, i) in detail.chapters" :key="ch.chid" class="chapter-item">
            <span class="idx">{{ i + 1 }}</span>
            <span class="name" @click="handlePlay(ch)">{{ ch.chapterName }}</span>
            <span class="summary">{{ ch.summary || '' }}</span>
            <div class="chapter-actions" @click.stop>
              <el-button size="small" type="primary" plain @click="handlePlay(ch)">视频</el-button>
              <el-button size="small" type="success" plain :disabled="!ch.fileCount" @click="openFiles(ch)">
                文件{{ ch.fileCount ? ' (' + ch.fileCount + ')' : '' }}
              </el-button>
              <el-button size="small" type="warning" plain :disabled="!ch.quizCount" @click="goQuiz(ch)">
                小测{{ ch.quizCount ? ' (' + ch.quizCount + ')' : '' }}
              </el-button>
            </div>
          </div>
        </div>
      </el-card>

      <el-dialog v-model="filesVisible" :title="filesTitle" width="520px">
        <el-empty v-if="!files.length" description="本章暂无文件" />
        <div v-else class="file-list">
          <div v-for="f in files" :key="f.fileId" class="file-item">
            <span class="f-name">{{ f.fileName }}</span>
            <span class="f-size">{{ formatSize(f.fileSize) }}</span>
            <a class="f-link" :href="f.fileUrl" target="_blank" rel="noopener">查看 / 下载</a>
          </div>
        </div>
      </el-dialog>

      <el-card class="rating-card">
        <template #header>
          <div class="rating-head">
            <span>课程评价</span>
            <span v-if="detail.myRating" class="my-rate">我的评分：{{ detail.myRating }} 星（可修改）</span>
          </div>
        </template>

        <div class="rating-form" v-if="isLogin">
          <div class="form-row">
            <el-rate v-model="myRate" />
            <span class="rate-tip">点击星星评分（1-5 星）</span>
          </div>
          <el-input v-model="comment" type="textarea" :rows="2" maxlength="500" placeholder="说说你对这门课的看法（选填）" />
          <div class="form-actions">
            <el-button type="primary" :loading="submitting" @click="submitRating">提交评价</el-button>
          </div>
        </div>
        <el-empty v-else description="登录后即可评价课程">
          <el-button type="primary" plain @click="$router.push('/login')">去登录</el-button>
        </el-empty>

        <el-divider content-position="left">全部评价</el-divider>
        <div v-if="detail.ratings && detail.ratings.length" class="rating-list">
          <div v-for="r in detail.ratings" :key="r.rid" class="rating-item">
            <div class="rating-meta">
              <span class="r-name">{{ r.userName }}</span>
              <el-rate :model-value="r.rating" disabled class="r-stars" />
              <span class="r-time">{{ formatTime(r.createTime) }}</span>
            </div>
            <div v-if="r.comment" class="r-comment">{{ r.comment }}</div>
          </div>
        </div>
        <el-empty v-else description="还没有评价，来抢沙发" />
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { getCourseDetail } from '../api/course'
import { getFrontCategories } from '../api/category'
import { collectCourse, cancelCollection } from '../api/collection'
import { rateCourse } from '../api/rating'
import { getChapterFiles } from '../api/chapterFile'

const route = useRoute()
const router = useRouter()
const cuid = route.params.cuid

const loading = ref(false)
const detail = ref(null)
const isLogin = ref(!!sessionStorage.getItem('loginUserId'))
const categories = ref([])

const myRate = ref(0)
const comment = ref('')
const submitting = ref(false)
const filesVisible = ref(false)
const files = ref([])
const filesTitle = ref('')

const openFiles = async (ch) => {
  filesTitle.value = `章节课件 · ${ch.chapterName}`
  filesVisible.value = true
  const res = await getChapterFiles(ch.chid)
  files.value = res.data || []
}

const goQuiz = (ch) => {
  router.push({ path: `/quiz/${ch.chid}`, query: { name: ch.chapterName } })
}

const formatSize = (s) => {
  if (!s) return ''
  if (s < 1024) return s + ' B'
  if (s < 1024 * 1024) return (s / 1024).toFixed(1) + ' KB'
  return (s / 1024 / 1024).toFixed(1) + ' MB'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCourseDetail(cuid)
    detail.value = res.data
    myRate.value = res.data.myRating || 0
  } finally {
    loading.value = false
  }
}

const categoryName = (cid) => {
  const c = categories.value.find((x) => x.cid === cid)
  return c ? c.categoryName : '-'
}

const formatTime = (t) => (t ? String(t).replace('T', ' ').slice(0, 19) : '')

const handleCollect = async () => {
  if (!isLogin.value) {
    router.push('/login')
    return
  }
  if (detail.value.collected) {
    await cancelCollection(cuid)
    detail.value.collected = false
    ElMessage.success('已取消收藏')
  } else {
    await collectCourse(cuid)
    detail.value.collected = true
    ElMessage.success('收藏成功')
  }
}

const handlePlay = (ch) => {
  router.push(`/play/${ch.chid}`)
}

const submitRating = async () => {
  if (!myRate.value) {
    ElMessage.warning('请先选择评分')
    return
  }
  submitting.value = true
  try {
    await rateCourse({ cuid: Number(cuid), rating: myRate.value, comment: comment.value.trim() })
    ElMessage.success('评价成功')
    loadData()
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  loadData()
  const res = await getFrontCategories()
  categories.value = res.data
})
</script>

<style scoped>
.info-card {
  margin-bottom: 16px;
}
.top {
  display: flex;
  gap: 20px;
}
.cover {
  width: 320px;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
  background: #e8edf2;
  flex-shrink: 0;
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.cover-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 15px;
  padding: 0 8px;
  text-align: center;
}
.main {
  flex: 1;
}
.main h2 {
  margin: 0 0 10px;
}
.tags {
  margin-bottom: 10px;
  display: flex;
  gap: 6px;
}
.meta-line {
  color: #606266;
  font-size: 14px;
  line-height: 2;
}
.actions {
  margin-top: 14px;
}
.explain {
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}
.rating-summary {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 14px;
}
.rate-stars {
  --el-rate-star-color: #f7ba2a;
}
.rate-text {
  font-size: 13px;
  color: #606266;
}
.rating-card {
  margin-top: 16px;
}
.rating-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.my-rate {
  font-size: 13px;
  color: #e6a23c;
}
.rating-form {
  margin-bottom: 8px;
}
.form-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}
.rate-tip {
  font-size: 13px;
  color: #909399;
}
.form-actions {
  text-align: right;
  margin-top: 10px;
}
.rating-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.rating-item {
  border: 1px solid #e6e6e6;
  border-radius: 6px;
  padding: 10px 12px;
}
.rating-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}
.r-name {
  font-size: 13px;
  font-weight: 600;
  width: 80px;
}
.r-stars {
  --el-rate-star-color: #f7ba2a;
}
.r-stars :deep(.el-rate__item) {
  margin-right: 2px;
}
.r-time {
  font-size: 12px;
  color: #909399;
}
.r-comment {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin-top: 6px;
}
.chapters {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.chapter-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border: 1px solid #e6e6e6;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}
.chapter-item:hover {
  background: #f5f7fa;
}
.idx {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  flex-shrink: 0;
}
.name {
  font-size: 14px;
  font-weight: 600;
}
.summary {
  flex: 1;
  color: #909399;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.chapter-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}
.file-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.file-item {
  display: flex;
  align-items: center;
  gap: 12px;
  border: 1px solid #e6e6e6;
  border-radius: 6px;
  padding: 10px 12px;
}
.f-name {
  flex: 1;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.f-size {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
}
.f-link {
  color: #409eff;
  font-size: 13px;
  text-decoration: none;
  flex-shrink: 0;
}
.f-link:hover {
  text-decoration: underline;
}
</style>
