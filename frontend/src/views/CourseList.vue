<template>
  <div class="course-list">
    <el-card class="filter-card">
      <div class="filters">
        <div class="cats">
          <span
            class="cat"
            :class="{ active: !query.cid }"
            @click="setCid(null)"
          >全部</span>
          <span
            v-for="c in categories"
            :key="c.cid"
            class="cat"
            :class="{ active: query.cid === c.cid }"
            @click="setCid(c.cid)"
          >{{ c.categoryName }}</span>
        </div>
        <div class="tools">
          <el-input v-model="query.keyword" placeholder="搜索课程" clearable style="width: 220px" @keyup.enter="loadData" @clear="loadData" />
          <el-select v-model="query.orderBy" style="width: 130px" @change="loadData">
            <el-option label="最新" value="latest" />
            <el-option label="最热" value="hot" />
          </el-select>
          <el-button type="primary" @click="loadData">搜索</el-button>
        </div>
      </div>
    </el-card>

    <div v-if="list.length" class="grid">
      <CourseCard v-for="c in list" :key="c.cuid" :course="c" />
    </div>
    <el-empty v-else-if="!loading" description="暂无课程" />

    <el-pagination
      class="pager"
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      :total="total"
      :page-sizes="[9, 18, 30]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadData"
      @current-change="loadData"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import CourseCard from '../components/CourseCard.vue'
import { getPortalCourses } from '../api/course'
import { getFrontCategories } from '../api/category'

const route = useRoute()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const categories = ref([])

const query = reactive({ pageNum: 1, pageSize: 9, cid: null, keyword: '', orderBy: 'latest' })

// 顶部导航搜索框跳转携带 keyword
watch(
  () => route.query.keyword,
  (kw) => {
    query.keyword = kw || ''
    query.pageNum = 1
    loadData()
  }
)

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      orderBy: query.orderBy,
      cid: query.cid || undefined,
      keyword: query.keyword || undefined
    }
    const res = await getPortalCourses(params)
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const setCid = (cid) => {
  query.cid = cid
  query.pageNum = 1
  loadData()
}

onMounted(async () => {
  loadData()
  const res = await getFrontCategories()
  categories.value = res.data
})
</script>

<style scoped>
.filter-card {
  margin-bottom: 16px;
}
.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.cats {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.cat {
  padding: 4px 12px;
  border-radius: 14px;
  font-size: 13px;
  cursor: pointer;
  color: #606266;
  border: 1px solid #dcdfe6;
}
.cat:hover {
  color: #409eff;
  border-color: #409eff;
}
.cat.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}
.tools {
  display: flex;
  gap: 8px;
}
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
.pager {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
