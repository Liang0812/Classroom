import request from './request'

// 前台：课程详情页查看章节课件（匿名）
export const getChapterFiles = (chid) => request.get(`/chapter-files/${chid}`)

// 管理端：管理员/教师管理章节课件
export const getAdminChapterFiles = (chid) => request.get(`/admin/chapters/${chid}/files`)
export const addChapterFile = (chid, data) => request.post(`/admin/chapters/${chid}/files`, data)
export const deleteChapterFile = (fileId) => request.delete(`/admin/chapters/files/${fileId}`)
