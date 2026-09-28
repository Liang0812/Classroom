import request from './request'

// 后台课程管理
export const getCoursePage = (params) => request.get('/admin/courses', { params })
export const addCourse = (data) => request.post('/admin/courses', data)
export const updateCourse = (cuid, data) => request.put(`/admin/courses/${cuid}`, data)
export const deleteCourse = (cuid) => request.delete(`/admin/courses/${cuid}`)
export const setCourseRecommend = (cuid, recommend) =>
  request.put(`/admin/courses/${cuid}/recommend`, null, { params: { recommend } })

// 前台课程
export const getPortalCourses = (params) => request.get('/courses', { params })
export const getCourseDetail = (cuid) => request.get(`/courses/${cuid}`)
