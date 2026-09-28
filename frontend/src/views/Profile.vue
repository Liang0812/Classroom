<template>
  <div class="profile">
    <el-card class="profile-card">
      <template #header>
        <div class="head">
          <div class="head-left">
            <div class="avatar-wrap" @click="pickAvatar" title="更换头像">
              <img v-if="user.avatar" :src="user.avatar" class="avatar" alt="头像" />
              <span v-else class="avatar avatar-fallback">{{ (user.userName || '?').charAt(0) }}</span>
              <div class="avatar-mask">更换</div>
            </div>
            <div>
              <div class="title">个人中心</div>
              <div class="sub">{{ user.userName }} · {{ rolesText }}</div>
            </div>
          </div>
        </div>
      </template>
      <input ref="fileInput" type="file" accept="image/*" class="hidden-input" @change="onAvatarChange" />
      <el-tabs v-model="activeTab">
        <!-- 我的课程 -->
        <el-tab-pane label="我的课程" name="courses">
          <div v-if="progress.length" class="course-list">
            <div v-for="p in progress" :key="p.cuid" class="course-item">
              <div class="course-info">
                <span class="name">{{ p.courseName }}</span>
                <span class="meta">老师：{{ p.teacher }} · 已学 {{ p.learnedChapters }}/{{ p.totalChapters }} 章</span>
              </div>
              <div class="bar">
                <el-progress :percentage="p.progress" :stroke-width="10" />
              </div>
              <div class="actions">
                <el-button size="small" type="primary" plain @click="continueLearn(p)">继续学习</el-button>
                <el-button
                  v-if="p.progress === 100"
                  size="small"
                  type="success"
                  plain
                  @click="$router.push(`/certificate/${p.cuid}`)"
                >领取证书</el-button>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无学习记录，去课程列表看看吧" />
        </el-tab-pane>

        <!-- 我的收藏 -->
        <el-tab-pane label="我的收藏" name="collections">
          <div v-if="collections.length" class="grid">
            <div v-for="c in collections" :key="c.coid" class="col-card" @click="$router.push(`/courses/${c.cuid}`)">
              <div class="cover">
                <img v-if="c.courseImage" :src="c.courseImage" :alt="c.courseName" />
                <div v-else class="cover-placeholder">{{ c.courseName }}</div>
              </div>
              <div class="info">
                <div class="name">{{ c.courseName }}</div>
                <div class="meta">老师：{{ c.teacher }} · 课时：{{ c.learnTime }}</div>
                <el-button size="small" type="danger" plain class="cancel" @click.stop="handleCancel(c)">取消收藏</el-button>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无收藏课程" />
        </el-tab-pane>

        <!-- 我的提问 -->
        <el-tab-pane label="我的提问" name="questions">
          <el-table :data="questions" v-loading="questionsLoading" stripe>
            <el-table-column prop="courseName" label="课程" min-width="130" show-overflow-tooltip />
            <el-table-column prop="chapterName" label="章节" min-width="130" show-overflow-tooltip />
            <el-table-column prop="question" label="问题" min-width="200" show-overflow-tooltip />
            <el-table-column prop="answer" label="回答" min-width="180" show-overflow-tooltip>
              <template #default="{ row }">{{ row.answer || '等待回答…' }}</template>
            </el-table-column>
            <el-table-column label="状态" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 2 ? 'success' : 'warning'" size="small">
                  {{ row.status === 2 ? '已回答' : '待回答' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!questionsLoading && !questions.length" description="暂无提问" />
        </el-tab-pane>

        <!-- 个人信息 -->
        <el-tab-pane label="个人信息" name="info">
          <el-form label-width="90px" class="info-form">
            <el-form-item label="用户名">
              <el-input :model-value="user.userName" disabled />
            </el-form-item>
            <el-form-item label="性别">
              <el-input :model-value="user.gender" disabled />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input :model-value="user.email" disabled />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input :model-value="user.phone" disabled />
            </el-form-item>
            <el-divider content-position="left">修改密码</el-divider>
            <el-form-item label="原密码">
              <el-input v-model="pwd.oldPassword" type="password" show-password placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwd.newPassword" type="password" show-password placeholder="不少于 6 位" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingPwd" @click="submitPwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { currentUser, changePassword, uploadAvatar } from '../api/auth'
import { getLearnProgress } from '../api/learn'
import { getMyCollections, cancelCollection } from '../api/collection'
import { getMyQuestions } from '../api/question'

const router = useRouter()
const activeTab = ref('courses')

const user = ref({})
const rolesText = ref(sessionStorage.getItem('loginUserRoles') || '')

const progress = ref([])
const collections = ref([])
const questions = ref([])
const questionsLoading = ref(false)
const savingPwd = ref(false)
const pwd = reactive({ oldPassword: '', newPassword: '' })
const fileInput = ref(null)
const avatarUploading = ref(false)

const pickAvatar = () => fileInput.value && fileInput.value.click()

const onAvatarChange = async (e) => {
  const file = e.target.files && e.target.files[0]
  e.target.value = ''
  if (!file) return
  avatarUploading.value = true
  try {
    const res = await uploadAvatar(file)
    user.value.avatar = res.data
    sessionStorage.setItem('loginUserAvatar', res.data)
    ElMessage.success('头像更新成功')
  } catch (err) {
    ElMessage.error(err && err.message ? err.message : '上传失败')
  } finally {
    avatarUploading.value = false
  }
}

const handleCancel = async (c) => {
  await cancelCollection(c.cuid)
  ElMessage.success('已取消收藏')
  collections.value = collections.value.filter((x) => x.coid !== c.coid)
}

const continueLearn = (p) => {
  if (p.lastChapterId) {
    router.push(`/play/${p.lastChapterId}`)
  } else {
    router.push(`/courses/${p.cuid}`)
  }
}

const submitPwd = async () => {
  if (!pwd.oldPassword || !pwd.newPassword) {
    ElMessage.warning('请填写原密码和新密码')
    return
  }
  if (pwd.newPassword.length < 6) {
    ElMessage.warning('新密码长度不少于 6 位')
    return
  }
  savingPwd.value = true
  try {
    await changePassword({ oldPassword: pwd.oldPassword, newPassword: pwd.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    sessionStorage.clear()
    router.push('/login')
  } finally {
    savingPwd.value = false
  }
}

onMounted(async () => {
  const res = await currentUser()
  user.value = res.data.user
  rolesText.value = res.data.roles.join('、')
  sessionStorage.setItem('loginRoleIds', (res.data.roleIds || []).join(','))
  sessionStorage.setItem('loginUserRoles', (res.data.roles || []).join(','))

  const [p, c] = await Promise.all([getLearnProgress(), getMyCollections()])
  progress.value = p.data || []
  collections.value = c.data || []

  questionsLoading.value = true
  try {
    const q = await getMyQuestions()
    questions.value = q.data || []
  } finally {
    questionsLoading.value = false
  }
})
</script>

<style scoped>
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.head-left {
  display: flex;
  align-items: center;
  gap: 14px;
}
.head-left .title {
  font-size: 16px;
  font-weight: 600;
}
.avatar-wrap {
  position: relative;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  flex-shrink: 0;
}
.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  border-radius: 50%;
}
.avatar-fallback {
  background: #4f6b9a;
  color: #fff;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
  border-radius: 50%;
}
.avatar-wrap:hover .avatar-mask {
  opacity: 1;
}
.hidden-input {
  display: none;
}
.sub {
  color: #909399;
  font-size: 13px;
}
.course-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.course-item {
  display: flex;
  align-items: center;
  gap: 20px;
}
.course-info {
  width: 260px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.course-info .name {
  font-size: 15px;
  font-weight: 600;
}
.course-info .meta {
  font-size: 12px;
  color: #909399;
}
.bar {
  flex: 1;
}
.actions {
  width: 200px;
  flex-shrink: 0;
  display: flex;
  gap: 8px;
}
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
.col-card {
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.col-card:hover {
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.1);
}
.cover {
  height: 120px;
  background: #e8edf2;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.cover-placeholder {
  color: #909399;
  font-size: 13px;
  padding: 0 8px;
  text-align: center;
}
.info {
  padding: 10px 12px;
}
.name {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.meta {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}
.cancel {
  width: 100%;
}
.info-form {
  max-width: 460px;
}
</style>
