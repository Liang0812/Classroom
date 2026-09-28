<template>
  <div class="role-manage">
    <el-card>
      <template #header>
        <div class="head">
          <span>角色权限管理</span>
          <div class="hint">选择角色后勾选其可访问的功能节点，保存后该角色后台菜单与接口权限立即生效</div>
        </div>
      </template>
      <el-radio-group v-model="currentRid" @change="loadRoleNodes">
        <el-radio-button v-for="r in roles" :key="r.rid" :value="r.rid">{{ r.roleName }}</el-radio-button>
      </el-radio-group>

      <el-divider />

      <div v-loading="loadingNodes" class="nodes">
        <el-checkbox
          v-for="n in nodes"
          :key="n.nid"
          v-model="checkedNids"
          :value="n.nid"
          :label="n.nid"
          class="node-check"
        >
          {{ n.nodeName }}
          <span class="node-url">{{ n.url }}</span>
        </el-checkbox>
      </div>

      <div class="actions">
        <el-button type="primary" :loading="saving" :disabled="!currentRid" @click="submit">保存角色权限</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { getRoles, getNodes, getRoleNodes, saveRoleNodes } from '../../api/menu'

const roles = ref([])
const nodes = ref([])
const currentRid = ref(null)
const checkedNids = ref([])
const loadingNodes = ref(false)
const saving = ref(false)

const loadRoleNodes = async () => {
  if (!currentRid.value) return
  loadingNodes.value = true
  try {
    const res = await getRoleNodes(currentRid.value)
    checkedNids.value = res.data || []
  } finally {
    loadingNodes.value = false
  }
}

const submit = async () => {
  saving.value = true
  try {
    await saveRoleNodes(currentRid.value, checkedNids.value)
    ElMessage.success('角色权限已保存')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const [r, n] = await Promise.all([getRoles(), getNodes()])
  roles.value = r.data || []
  nodes.value = n.data || []
  if (roles.value.length) {
    currentRid.value = roles.value[0].rid
    loadRoleNodes()
  }
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
.nodes {
  min-height: 120px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.node-check {
  display: flex;
  align-items: center;
}
.node-url {
  color: #909399;
  font-size: 12px;
  margin-left: 6px;
}
.actions {
  margin-top: 20px;
}
</style>
