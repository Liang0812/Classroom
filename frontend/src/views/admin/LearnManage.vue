<template>
  <div class="learn-manage">
    <el-card>
      <template #header>学习记录</template>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="userName" label="学员" width="110" />
        <el-table-column prop="courseName" label="课程" min-width="140" show-overflow-tooltip />
        <el-table-column prop="chapterName" label="章节" min-width="140" show-overflow-tooltip />
        <el-table-column label="开始时间" width="170">
          <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" width="170">
          <template #default="{ row }">{{ row.endTime ? formatTime(row.endTime) : '学习中…' }}</template>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLearnAdminPage } from '../../api/learn'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10 })

const formatTime = (t) => (t ? String(t).replace('T', ' ').slice(0, 19) : '')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getLearnAdminPage({ pageNum: query.pageNum, pageSize: query.pageSize })
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
