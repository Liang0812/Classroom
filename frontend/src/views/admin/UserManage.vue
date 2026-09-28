<template>
  <div class="user-manage">
    <el-card>
      <template #header>
        <div class="head">
          <span>用户管理（角色分配）</span>
          <div class="tools">
            <el-select v-model="query.role" placeholder="角色" clearable style="width: 130px" @change="loadData">
              <el-option v-for="r in roles" :key="r.rid" :label="r.roleName" :value="r.rid" />
            </el-select>
            <el-input v-model="query.keyword" placeholder="用户名/邮箱/手机号" clearable style="width: 220px" @keyup.enter="loadData" @clear="loadData" />
            <el-button type="primary" @click="loadData">搜索</el-button>
          </div>
        </div>
      </template>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="uid" label="ID" width="60" />
        <el-table-column prop="userName" label="用户名" min-width="110" />
        <el-table-column prop="gender" label="性别" width="70" align="center" />
        <el-table-column prop="email" label="邮箱" min-width="170" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="roleNames" label="角色" min-width="150">
          <template #default="{ row }">
            <el-tag v-for="r in splitRoles(row.roleNames)" :key="r" size="small" class="role-tag">{{ r }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" align="center">
          <template #default="{ row }">
            <template v-if="row.uid !== myUid">
              <el-button size="small" type="primary" plain @click="openAssign(row)">分配角色</el-button>
              <el-button size="small" :type="row.status === 1 ? 'danger' : 'success'" plain @click="handleStatus(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button size="small" type="warning" plain @click="handleResetPwd(row)">重置密码</el-button>
            </template>
            <span v-else class="self">自己</span>
          </template>
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

    <el-dialog v-model="assignVisible" title="分配角色" width="420px" @closed="resetAssign">
      <el-descriptions :column="1" border v-if="current">
        <el-descriptions-item label="用户">{{ current.userName }}（{{ current.email }}）</el-descriptions-item>
      </el-descriptions>
      <el-checkbox-group v-model="selectedRoleIds" class="role-checks">
        <el-checkbox v-for="r in roles" :key="r.rid" :value="r.rid" :label="r.rid">{{ r.roleName }}</el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitAssign">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { ElMessageBox } from 'element-plus/es/components/message-box/index.mjs'
import { getUsersPage, assignUserRoles, updateUserStatus, resetUserPassword } from '../../api/user'
import { getRoles } from '../../api/menu'

// 当前登录用户 ID（模板中无法直接访问全局 sessionStorage）
const myUid = Number(sessionStorage.getItem('loginUserId')) || 0

const loading = ref(false)
const list = ref([])
const total = ref(0)
const roles = ref([])
const query = reactive({ pageNum: 1, pageSize: 10, role: null, keyword: '' })

const assignVisible = ref(false)
const current = ref(null)
const selectedRoleIds = ref([])
const saving = ref(false)

const splitRoles = (s) => (s ? s.split(',') : [])

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      role: query.role || undefined,
      keyword: query.keyword || undefined
    }
    const res = await getUsersPage(params)
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const openAssign = (row) => {
  current.value = row
  selectedRoleIds.value = splitRoles(row.roleNames).map((name) => {
    const r = roles.value.find((x) => x.roleName === name)
    return r ? r.rid : null
  }).filter((x) => x !== null)
  assignVisible.value = true
}

const resetAssign = () => {
  current.value = null
  selectedRoleIds.value = []
}

const submitAssign = async () => {
  if (!selectedRoleIds.value.length) {
    ElMessage.warning('至少选择一个角色')
    return
  }
  saving.value = true
  try {
    await assignUserRoles(current.value.uid, selectedRoleIds.value)
    ElMessage.success('角色分配成功')
    assignVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  await ElMessageBox.confirm(`确认${action}用户「${row.userName}」？禁用后该账号将无法登录。`, '提示', { type: 'warning' })
  await updateUserStatus(row.uid, row.status === 1 ? 0 : 1)
  ElMessage.success(`${action}成功`)
  loadData()
}

const handleResetPwd = async (row) => {
  const { value } = await ElMessageBox.prompt(
    `请输入「${row.userName}」的新密码（至少 6 位）`,
    '重置密码',
    {
      inputPattern: /^.{6,}$/,
      inputErrorMessage: '密码长度至少 6 位',
      confirmButtonText: '确认重置'
    }
  )
  await resetUserPassword(row.uid, value)
  ElMessage.success('密码已重置')
}

onMounted(async () => {
  loadData()
  const res = await getRoles()
  roles.value = res.data || []
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
.role-tag {
  margin-right: 4px;
}
.self {
  color: #909399;
  font-size: 13px;
}
.role-checks {
  margin-top: 16px;
}
</style>
