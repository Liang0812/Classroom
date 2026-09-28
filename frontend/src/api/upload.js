import request from './request'

// 文件上传（管理员/老师）：返回 /upload/... 访问 URL
export const uploadVideo = (file) => {
  const form = new FormData()
  form.append('file', file)
  return request.post('/admin/upload/video', form)
}

export const uploadImage = (file) => {
  const form = new FormData()
  form.append('file', file)
  return request.post('/admin/upload/image', form)
}

export const uploadFile = (file) => {
  const form = new FormData()
  form.append('file', file)
  return request.post('/admin/upload/file', form)
}
