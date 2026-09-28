<template>
  <div class="front-layout">
    <header class="nav">
      <div class="nav-inner">
        <div class="logo" @click="$router.push('/')">酷云课堂</div>
        <nav class="menu">
          <router-link to="/" class="item" :class="{ active: $route.path === '/' }">首页</router-link>
          <router-link to="/courses" class="item" :class="{ active: $route.path.startsWith('/courses') }">课程列表</router-link>
        </nav>
        <div class="search">
          <el-input
            v-model="keyword"
            placeholder="搜索课程"
            clearable
            size="small"
            class="search-input"
            @keyup.enter="doSearch"
            @clear="doSearch"
          />
        </div>
        <div class="user">
          <template v-if="isLogin">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" class="msg-badge">
              <el-button size="small" circle @click="$router.push('/messages')" title="我的消息">
                <span class="bell">🔔</span>
              </el-button>
            </el-badge>
            <el-button v-if="hasAdmin" size="small" type="primary" plain @click="$router.push('/admin')">后台管理</el-button>
            <el-dropdown trigger="click" @command="handleAccountCommand">
              <span class="account">
                <img v-if="avatar" :src="avatar" class="avatar-img" alt="头像" />
                <span v-else class="avatar-fallback">{{ (userName || '?').charAt(0) }}</span>
                <span class="name">{{ userName }}</span>
                <span class="arrow">▾</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="my-questions">我的提问</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button size="small" type="primary" @click="$router.push('/login')">登录</el-button>
            <el-button size="small" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    <main class="content">
      <router-view />
    </main>
    <footer class="footer">酷云课堂 · 在线教学系统</footer>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { currentUser, logout } from '../api/auth'
import { getUnreadCount } from '../api/message'

const router = useRouter()
const route = useRoute()
const isLogin = ref(!!sessionStorage.getItem('loginUserId'))
const userName = ref(sessionStorage.getItem('loginUserName') || '')
const avatar = ref(sessionStorage.getItem('loginUserAvatar') || '')
const unreadCount = ref(0)
const hasAdmin = ref(hasAdminRole())
const keyword = ref('')

const doSearch = () => {
  const kw = keyword.value.trim()
  router.push({ path: '/courses', query: kw ? { keyword: kw } : {} })
}

// 账号下拉菜单：个人中心 / 我的提问 / 退出登录
const handleAccountCommand = (cmd) => {
  if (cmd === 'profile') router.push('/profile')
  else if (cmd === 'my-questions') router.push('/my-questions')
  else if (cmd === 'logout') handleLogout()
}

function hasAdminRole() {
  const ids = sessionStorage.getItem('loginRoleIds')
  if (!ids) return false
  return ids.split(',').map(Number).some((id) => id === 1 || id === 2)
}

const refreshUnread = async () => {
  if (!isLogin.value) return
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (e) {
    // 忽略：未登录态由登录拦截器处理
  }
}

onMounted(async () => {
  if (isLogin.value) {
    try {
      const res = await currentUser()
      userName.value = res.data.user.userName
      sessionStorage.setItem('loginRoleIds', (res.data.roleIds || []).join(','))
      sessionStorage.setItem('loginUserAvatar', res.data.user.avatar || '')
      avatar.value = res.data.user.avatar || ''
      hasAdmin.value = hasAdminRole()
    } catch (e) {
      isLogin.value = false
      sessionStorage.clear()
    }
  }
  refreshUnread()
})

// 回到前台页面时刷新未读数
watch(
  () => route.fullPath,
  () => refreshUnread()
)

const handleLogout = async () => {
  await logout()
  sessionStorage.clear()
  isLogin.value = false
  unreadCount.value = 0
  hasAdmin.value = false
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.nav {
  background: #1f2d3d;
  color: #fff;
}
.nav-inner {
  max-width: 1100px;
  margin: 0 auto;
  height: 56px;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 16px;
}
.logo {
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
}
.menu {
  display: flex;
  gap: 8px;
}
.menu .item {
  color: #cfd8e3;
  text-decoration: none;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
}
.menu .item:hover,
.menu .item.active {
  color: #fff;
  background: rgba(255, 255, 255, 0.12);
}
.search {
  width: 200px;
}
.search-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.14);
  box-shadow: none;
}
.search-input :deep(.el-input__inner) {
  color: #fff;
}
.search-input :deep(.el-input__inner::placeholder) {
  color: #a5b4c5;
}
.search-input :deep(.el-input__clear) {
  color: #cfd8e3;
}
.user {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}
.user .name {
  color: #e6f7ff;
  font-size: 14px;
}
.account {
  color: #e6f7ff;
  font-size: 14px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  border-radius: 4px;
  user-select: none;
}
.account:hover {
  background: rgba(255, 255, 255, 0.12);
}
.avatar-img {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  object-fit: cover;
  vertical-align: middle;
}
.avatar-fallback {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #4f6b9a;
  color: #fff;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.arrow {
  font-size: 12px;
  color: #a5b4c5;
}
.bell {
  font-size: 15px;
}
.content {
  flex: 1;
  max-width: 1100px;
  width: 100%;
  margin: 0 auto;
  padding: 16px;
  box-sizing: border-box;
}
.footer {
  text-align: center;
  color: #909399;
  font-size: 13px;
  padding: 16px 0;
  border-top: 1px solid #e6e6e6;
}
</style>
