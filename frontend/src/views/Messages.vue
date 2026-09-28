<template>
  <div class="messages">
    <el-card>
      <template #header>我的消息</template>
      <el-table :data="list" v-loading="loading" stripe @row-click="openDetail">
        <el-table-column label="标题" min-width="180">
          <template #default="{ row }">
            <span class="msg-title" :class="{ unread: isUnread(row) }">{{ row.title }}</span>
            <el-tag v-if="isUnread(row)" type="danger" size="small">未读</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发送人" width="120">
          <template #default="{ row }">{{ row.senderName || '系统' }}</template>
        </el-table-column>
        <el-table-column label="接收范围" width="120">
          <template #default="{ row }">
            {{ row.receiverUid === 0 ? '全体学员' : '定向通知' }}
          </template>
        </el-table-column>
        <el-table-column label="发送时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && !list.length" description="暂无消息" />
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

    <el-drawer v-model="drawerVisible" title="消息详情" size="420px">
      <template v-if="detail">
        <h3 class="detail-title">{{ detail.title }}</h3>
        <div class="detail-meta">
          发送人：{{ detail.senderName || '系统' }} · {{ formatTime(detail.createTime) }}
        </div>
        <el-divider />
        <p class="detail-content">{{ detail.content }}</p>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyMessages, getMessageDetail } from '../api/message'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10 })

const drawerVisible = ref(false)
const detail = ref(null)

const isUnread = (row) => row.isRead === 0 && row.receiverUid !== 0

const formatTime = (t) => (t ? String(t).replace('T', ' ').slice(0, 19) : '')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyMessages({ pageNum: query.pageNum, pageSize: query.pageSize })
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const openDetail = async (row) => {
  const res = await getMessageDetail(row.mid)
  detail.value = res.data
  drawerVisible.value = true
  // 详情返回后若已读标记变化则刷新列表（定向未读会自动标已读）
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.msg-title {
  margin-right: 6px;
}
.msg-title.unread {
  font-weight: 700;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.detail-title {
  margin: 0 0 8px;
}
.detail-meta {
  color: #909399;
  font-size: 13px;
}
.detail-content {
  color: #303133;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
