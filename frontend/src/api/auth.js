import request from './request'

// 认证相关接口
export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)
export const logout = () => request.post('/auth/logout')
export const currentUser = () => request.get('/auth/current')
export const changePassword = (data) => request.post('/auth/password', data)

// 上传个人头像（登录用户）：返回 /upload/avatar/... URL
export const uploadAvatar = (file) => {
  const form = new FormData()
  form.append('file', file)
  return request.post('/auth/avatar', form)
}

// 后台用户查询（消息接收人选择等）
export const getUsersByRole = (role) => request.get('/admin/users', { params: { role } })
