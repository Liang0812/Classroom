import request from './request'

// 回收站：分类
export const getRecycleCategories = () => request.get('/admin/recycle/categories')
export const restoreCategory = (cid) => request.post(`/admin/recycle/categories/${cid}/restore`)
export const deleteCategory = (cid) => request.delete(`/admin/recycle/categories/${cid}`)

// 回收站：课程
export const getRecycleCourses = () => request.get('/admin/recycle/courses')
export const restoreCourse = (cuid) => request.post(`/admin/recycle/courses/${cuid}/restore`)
export const deleteCourse = (cuid) => request.delete(`/admin/recycle/courses/${cuid}`)

// 回收站：章节
export const getRecycleChapters = () => request.get('/admin/recycle/chapters')
export const restoreChapter = (chid) => request.post(`/admin/recycle/chapters/${chid}/restore`)
export const deleteChapter = (chid) => request.delete(`/admin/recycle/chapters/${chid}`)

// 回收站：提问
export const getRecycleQuestions = () => request.get('/admin/recycle/questions')
export const restoreQuestion = (qid) => request.post(`/admin/recycle/questions/${qid}/restore`)
export const deleteQuestion = (qid) => request.delete(`/admin/recycle/questions/${qid}`)
