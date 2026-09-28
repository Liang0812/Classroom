<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col v-for="s in summaryItems" :key="s.label" :xs="12" :sm="8" :md="4">
        <el-card class="stat-card">
          <div class="stat-num">{{ s.value }}</div>
          <div class="stat-label">{{ s.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="lower">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>热门课程 TOP5（按学习次数）</template>
          <el-table :data="hotCourses" stripe size="small">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="courseName" label="课程" min-width="140" show-overflow-tooltip />
            <el-table-column prop="teacher" label="老师" width="100" />
            <el-table-column prop="clicked" label="学习次数" width="90" align="center" />
          </el-table>
          <el-empty v-if="!hotCourses.length" description="暂无课程" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>近 7 天学习记录趋势</template>
          <div class="trend">
            <div v-for="(t, i) in trendDays" :key="t.day" class="trend-col">
              <div class="trend-bar" :style="{ height: barHeight(t.count) }" :title="`${t.day}：${t.count} 次`"></div>
              <div class="trend-label">{{ t.day.slice(5) }}</div>
            </div>
          </div>
          <el-empty v-if="!trendDays.length" description="暂无学习记录" />
        </el-card>
      </el-col>
    </el-row>

    <el-card class="lower">
      <template #header>各分类课程分布</template>
      <div v-if="categoryData.length" class="cats">
        <div v-for="c in categoryData" :key="c.cid" class="cat-row">
          <span class="cat-name">{{ c.categoryName }}</span>
          <div class="cat-bar">
            <div class="cat-fill" :style="{ width: catPercent(c.courseCount) }"></div>
          </div>
          <span class="cat-count">{{ c.courseCount }} 门</span>
        </div>
      </div>
      <el-empty v-else description="暂无分类" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStatsSummary, getHotCourses, getLearnTrend, getCategoryCourses } from '../../api/stats'

const summary = ref({})
const hotCourses = ref([])
const trend = ref([])
const categoryData = ref([])

const summaryItems = computed(() => [
  { label: '学员用户', value: summary.value.users ?? '-' },
  { label: '课程总数', value: summary.value.courses ?? '-' },
  { label: '课程分类', value: summary.value.categories ?? '-' },
  { label: '学习记录', value: summary.value.learns ?? '-' },
  { label: '学员提问', value: summary.value.questions ?? '-' },
  { label: '站内消息', value: summary.value.messages ?? '-' }
])

const trendDays = computed(() => {
  const days = trend.value
  if (!days.length) return []
  // 补齐中间缺日
  const byDay = {}
  days.forEach((d) => (byDay[d.day] = d.count))
  const list = []
  const start = new Date(days[0].day)
  const end = new Date(days[days.length - 1].day)
  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    const key = d.toISOString().slice(0, 10)
    list.push({ day: key, count: byDay[key] || 0 })
  }
  return list
})

const maxCount = computed(() => Math.max(1, ...trendDays.value.map((t) => t.count)))

const barHeight = (count) => `${Math.max(4, Math.round((count / maxCount.value) * 120))}px`

const maxCat = computed(() => Math.max(1, ...categoryData.value.map((c) => c.courseCount)))

const catPercent = (count) => `${Math.round((count / maxCat.value) * 100)}%`

onMounted(async () => {
  const [s, h, t, c] = await Promise.all([getStatsSummary(), getHotCourses(5), getLearnTrend(7), getCategoryCourses()])
  summary.value = s.data || {}
  hotCourses.value = h.data || []
  trend.value = t.data || []
  categoryData.value = c.data || []
})
</script>

<style scoped>
.stat-card {
  text-align: center;
  margin-bottom: 16px;
}
.stat-num {
  font-size: 28px;
  font-weight: 700;
  color: #409eff;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
.lower {
  margin-top: 8px;
}
.trend {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  height: 150px;
  padding-top: 10px;
}
.trend-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  gap: 6px;
}
.trend-bar {
  width: 100%;
  max-width: 40px;
  background: linear-gradient(180deg, #79bbff, #409eff);
  border-radius: 4px 4px 0 0;
}
.trend-label {
  font-size: 11px;
  color: #909399;
}
.cats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.cat-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.cat-name {
  width: 130px;
  font-size: 14px;
  color: #606266;
  text-align: right;
  flex-shrink: 0;
}
.cat-bar {
  flex: 1;
  height: 18px;
  background: #f0f2f5;
  border-radius: 9px;
  overflow: hidden;
}
.cat-fill {
  height: 100%;
  background: linear-gradient(90deg, #79bbff, #409eff);
  border-radius: 9px;
  min-width: 6px;
}
.cat-count {
  width: 60px;
  font-size: 13px;
  color: #606266;
  flex-shrink: 0;
}
</style>
