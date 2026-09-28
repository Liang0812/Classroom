import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('../views/FrontLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { public: true }
      },
      {
        path: 'courses',
        name: 'CourseList',
        component: () => import('../views/CourseList.vue'),
        meta: { public: true }
      },
      {
        path: 'courses/:cuid',
        name: 'CourseDetail',
        component: () => import('../views/CourseDetail.vue'),
        meta: { public: true }
      },
      {
        path: 'play/:chid',
        name: 'PlayVideo',
        component: () => import('../views/PlayVideo.vue')
      },
      {
        path: 'quiz/:chid',
        name: 'QuizView',
        component: () => import('../views/QuizView.vue')
      },
      {
        path: 'my-questions',
        name: 'MyQuestions',
        component: () => import('../views/MyQuestions.vue')
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('../views/Messages.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue')
      },
      {
        path: 'certificate/:cuid',
        name: 'Certificate',
        component: () => import('../views/Certificate.vue')
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    redirect: '/admin/index',
    children: [
      {
        path: 'index',
        name: 'Dashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'categories',
        name: 'CategoryManage',
        component: () => import('../views/admin/CategoryManage.vue')
      },
      {
        path: 'courses',
        name: 'CourseManage',
        component: () => import('../views/admin/CourseManage.vue')
      },
      {
        path: 'courses/:cuid/chapters',
        name: 'ChapterManage',
        component: () => import('../views/admin/ChapterManage.vue')
      },
      {
        path: 'questions',
        name: 'QuestionManage',
        component: () => import('../views/admin/QuestionManage.vue')
      },
      {
        path: 'messages',
        name: 'MessageManage',
        component: () => import('../views/admin/MessageManage.vue')
      },
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('../views/admin/UserManage.vue')
      },
      {
        path: 'roles',
        name: 'RoleManage',
        component: () => import('../views/admin/RoleManage.vue')
      },
      {
        path: 'learn',
        name: 'LearnManage',
        component: () => import('../views/admin/LearnManage.vue')
      },
      {
        path: 'recycle',
        name: 'RecycleManage',
        component: () => import('../views/admin/RecycleManage.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 角色判断：管理员(1) 或 老师(2)
const hasAdminRole = () => {
  const ids = sessionStorage.getItem('loginRoleIds')
  if (!ids) return false
  return ids.split(',').map(Number).some((id) => id === 1 || id === 2)
}

// 登录 + 角色守卫
router.beforeEach((to, from, next) => {
  const uid = sessionStorage.getItem('loginUserId')
  if (to.path.startsWith('/admin')) {
    if (!uid) {
      next('/login')
      return
    }
    if (!hasAdminRole()) {
      ElMessage.error('无权访问后台')
      next('/')
      return
    }
    next()
    return
  }
  if (!uid && !to.meta.public) {
    next('/login')
  } else {
    next()
  }
})

export default router
