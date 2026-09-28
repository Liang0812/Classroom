<template>
  <div class="recycle-manage">
    <el-card>
      <template #header>
        <div class="head">
          <span>回收站</span>
          <span class="hint">恢复：数据回到正常列表；彻底删除：连同关联数据（学习记录/收藏/提问等）一并删除，不可恢复</span>
        </div>
      </template>
      <el-tabs v-model="activeTab" @tab-change="loadTab">
        <!-- 分类 -->
        <el-tab-pane label="分类回收站" name="categories">
          <el-table :data="cats" v-loading="loading" stripe>
            <el-table-column prop="cid" label="ID" width="60" />
            <el-table-column prop="categoryName" label="分类名" min-width="180" />
            <el-table-column prop="orders" label="次序" width="80" align="center" />
            <el-table-column label="删除时间" min-width="160">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template #default="{ row }">
                <el-button size="small" type="success" plain @click="restore('category', row.cid)">恢复</el-button>
                <el-button size="small" type="danger" plain @click="remove('category', row.cid)">彻底删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && !cats.length" description="暂无伪删除分类" />
        </el-tab-pane>

        <!-- 课程 -->
        <el-tab-pane label="课程回收站" name="courses">
          <el-table :data="courses" v-loading="loading" stripe>
            <el-table-column prop="cuid" label="ID" width="60" />
            <el-table-column prop="courseName" label="课程名" min-width="160" show-overflow-tooltip />
            <el-table-column prop="teacher" label="老师" width="110" />
            <el-table-column prop="chapterCount" label="章节数" width="80" align="center" />
            <el-table-column label="删除时间" min-width="160">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template #default="{ row }">
                <el-button size="small" type="success" plain @click="restore('course', row.cuid)">恢复</el-button>
                <el-button size="small" type="danger" plain @click="remove('course', row.cuid)">彻底删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && !courses.length" description="暂无伪删除课程" />
        </el-tab-pane>

        <!-- 章节 -->
        <el-tab-pane label="章节回收站" name="chapters">
          <el-table :data="chapters" v-loading="loading" stripe>
            <el-table-column prop="chid" label="ID" width="60" />
            <el-table-column prop="courseName" label="所属课程" min-width="150" show-overflow-tooltip />
            <el-table-column prop="chapterName" label="章节名" min-width="180" show-overflow-tooltip />
            <el-table-column label="删除时间" min-width="160">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template #default="{ row }">
                <el-button size="small" type="success" plain @click="restore('chapter', row.chid)">恢复</el-button>
                <el-button size="small" type="danger" plain @click="remove('chapter', row.chid)">彻底删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && !chapters.length" description="暂无伪删除章节" />
        </el-tab-pane>

        <!-- 提问 -->
        <el-tab-pane label="提问回收站" name="questions">
          <el-table :data="questions" v-loading="loading" stripe>
            <el-table-column prop="qid" label="ID" width="60" />
            <el-table-column prop="userName" label="提问学员" width="110" />
            <el-table-column prop="courseName" label="课程" min-width="140" show-overflow-tooltip />
            <el-table-column prop="chapterName" label="章节" min-width="140" show-overflow-tooltip />
            <el-table-column prop="question" label="问题" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="180" align="center">
              <template #default="{ row }">
                <el-button size="small" type="success" plain @click="restore('question', row.qid)">恢复</el-button>
                <el-button size="small" type="danger" plain @click="remove('question', row.qid)">彻底删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && !questions.length" description="暂无伪删除提问" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { ElMessageBox } from 'element-plus/es/components/message-box/index.mjs'
import {
  getRecycleCategories, restoreCategory, deleteCategory,
  getRecycleCourses, restoreCourse, deleteCourse,
  getRecycleChapters, restoreChapter, deleteChapter,
  getRecycleQuestions, restoreQuestion, deleteQuestion
} from '../../api/recycle'

const activeTab = ref('categories')
const loading = ref(false)
const cats = ref([])
const courses = ref([])
const chapters = ref([])
const questions = ref([])

const formatTime = (t) => (t ? String(t).replace('T', ' ').slice(0, 19) : '')

const loadTab = async () => {
  loading.value = true
  try {
    if (activeTab.value === 'categories') cats.value = (await getRecycleCategories()).data || []
    if (activeTab.value === 'courses') courses.value = (await getRecycleCourses()).data || []
    if (activeTab.value === 'chapters') chapters.value = (await getRecycleChapters()).data || []
    if (activeTab.value === 'questions') questions.value = (await getRecycleQuestions()).data || []
  } finally {
    loading.value = false
  }
}

const api = {
  category: { restore: restoreCategory, remove: deleteCategory },
  course: { restore: restoreCourse, remove: deleteCourse },
  chapter: { restore: restoreChapter, remove: deleteChapter },
  question: { restore: restoreQuestion, remove: deleteQuestion }
}

const restore = async (type, id) => {
  await api[type].restore(id)
  ElMessage.success('已恢复')
  loadTab()
}

const remove = (type, id) => {
  ElMessageBox.confirm(
    '彻底删除后不可恢复，关联的学习记录、收藏、提问等数据也会一并删除。确认继续？',
    '彻底删除确认',
    { type: 'warning', confirmButtonText: '彻底删除', cancelButtonText: '取消' }
  ).then(async () => {
    await api[type].remove(id)
    ElMessage.success('已彻底删除')
    loadTab()
  }).catch(() => {})
}

onMounted(() => {
  loadTab()
})
</script>

<style scoped>
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.hint {
  color: #909399;
  font-size: 12px;
}
</style>
