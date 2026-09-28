import request from './request'

// 章节管理
export const getChaptersByCourse = (cuid) => request.get(`/admin/chapters/course/${cuid}`)
export const addChapter = (data) => request.post('/admin/chapters', data)
export const updateChapter = (chid, data) => request.put(`/admin/chapters/${chid}`, data)
export const deleteChapter = (chid) => request.delete(`/admin/chapters/${chid}`)
