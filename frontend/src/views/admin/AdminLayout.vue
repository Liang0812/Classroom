<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="220px">
        <div class="logo">酷云课堂 · 后台</div>
        <el-menu :default-active="$route.path" router background-color="#1f2d3d" text-color="#cfd8e3" active-text-color="#409eff">
          <el-menu-item v-for="m in menus" :key="m.url" :index="m.route">
            <span>{{ m.nodeName }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <div class="topbar">
          <span>欢迎，{{ userName }}（{{ roleText }}）</span>
          <el-button size="small" @click="$router.push('/')">返回前台</el-button>
          <el-button size="small" type="danger" @click="handleLogout">退出</el-button>
        </div>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { currentUser, logout } from '../../api/auth'
import { getMenus } from '../../api/menu'

const router = useRouter()
const userName = ref(sessionStorage.getItem('loginUserName') || '')
const roleText = ref(sessionStorage.getItem('loginUserRoles') || '')
const menus = ref([])

// 节点 URL → 前端路由映射（无页面的节点不渲染，如"测试功能"）
const routeMap = {
  '/admin/index': '/admin/index',
  '/admin/category': '/admin/categories',
  '/admin/course': '/admin/courses',
  '/admin/chapter': '/admin/courses',
  '/admin/question': '/admin/questions',
  '/admin/message': '/admin/messages',
  '/admin/user': '/admin/users',
  '/admin/role': '/admin/roles',
  '/admin/node': '/admin/roles',
  '/admin/learn': '/admin/learn',
  '/admin/recycle': '/admin/recycle'
}

onMounted(async () => {
  try {
    const res = await currentUser()
    userName.value = res.data.user.userName
    sessionStorage.setItem('loginRoleIds', (res.data.roleIds || []).join(','))
    sessionStorage.setItem('loginUserRoles', (res.data.roles || []).join(','))
    roleText.value = res.data.roles.join('、')
  } catch (e) {
    router.push('/login')
    return
  }
  try {
    const res = await getMenus()
    const seen = new Set()
    menus.value = (res.data || [])
      .map((n) => ({ nodeName: n.nodeName, url: n.url, route: routeMap[n.url] }))
      .filter((m) => m.route && !seen.has(m.route) && seen.add(m.route))
  } catch (e) {
    menus.value = []
  }
})

const handleLogout = async () => {
  await logout()
  sessionStorage.clear()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}
.el-aside {
  background: #1f2d3d;
}
.logo {
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  text-align: center;
  padding: 16px 0;
}
.el-menu {
  border-right: none;
}
.topbar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e6e6e6;
  margin-bottom: 16px;
  color: #606266;
  font-size: 14px;
}
</style>
