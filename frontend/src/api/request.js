import axios from 'axios'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    ElMessage.error(res.message || '请求失败')
    if (res.code === 401) {
      sessionStorage.clear()
      router.push('/login')
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      ElMessage.error('未登录，请先登录')
      sessionStorage.clear()
      router.push('/login')
    } else if (error.response && error.response.status === 403) {
      ElMessage.error(error.response.data || '无权访问')
      router.push('/')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
