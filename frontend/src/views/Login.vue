<template>
  <div class="login-page">
    <video
      v-show="!bgFailed"
      class="bg-video"
      :src="bgVideo"
      autoplay
      muted
      loop
      playsinline
      preload="auto"
      @error="bgFailed = true"
    ></video>
    <div class="bg-mask"></div>
    <el-card class="login-card">
      <h2 class="title">酷云课堂</h2>
      <p class="subtitle">在线教学系统 · 登录</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="account">
          <el-input v-model="form.account" placeholder="邮箱或手机号" size="large" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="submit" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <el-link type="primary" @click="$router.push('/register')">没有账号？去注册</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { useRouter } from 'vue-router'
import { login } from '../api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const bgFailed = ref(false)
const bgVideo = '/video-bg.mp4'
const form = reactive({ account: '', password: '' })
const rules = {
  account: [{ required: true, message: '请输入邮箱或手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form)
    sessionStorage.setItem('loginUserId', res.data.user.uid)
    sessionStorage.setItem('loginUserName', res.data.user.userName)
    sessionStorage.setItem('loginUserRoles', (res.data.roles || []).join(','))
    sessionStorage.setItem('loginRoleIds', (res.data.roleIds || []).join(','))
    sessionStorage.setItem('loginUserAvatar', res.data.user.avatar || '')
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0e1420;
  position: relative;
  overflow: hidden;
}
.bg-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}
.bg-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.45);
  z-index: 1;
}
.login-card {
  position: relative;
  z-index: 2;
  width: 400px;
  padding: 16px 10px;
  background: rgba(255, 255, 255, 0.94);
  border-radius: 12px;
}
.title {
  text-align: center;
  margin: 0 0 4px;
}
.subtitle {
  text-align: center;
  color: #909399;
  margin-bottom: 24px;
  font-size: 13px;
}
.submit {
  width: 100%;
}
.footer {
  text-align: center;
  margin-top: 8px;
}
</style>
