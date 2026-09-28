<template>
  <div class="register-page">
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
    <el-card class="register-card">
      <h2 class="title">注册新账号</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="不少于 6 位" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="submit" :loading="loading" @click="handleRegister">注 册</el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <el-link type="primary" @click="$router.push('/login')">已有账号？去登录</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { useRouter } from 'vue-router'
import { register } from '../api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const bgFailed = ref(false)
const bgVideo = '/video-bg.mp4'
const form = reactive({
  userName: '',
  gender: '男',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const rules = {
  userName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不少于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const payload = {
      userName: form.userName,
      gender: form.gender,
      email: form.email,
      phone: form.phone,
      password: form.password
    }
    await register(payload)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
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
.register-card {
  position: relative;
  z-index: 2;
  width: 460px;
  padding: 16px 10px;
  background: rgba(255, 255, 255, 0.94);
  border-radius: 12px;
}
.title {
  text-align: center;
  margin: 0 0 24px;
}
.submit {
  width: 100%;
}
.footer {
  text-align: center;
  margin-top: 8px;
}
</style>
