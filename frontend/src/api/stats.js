import request from './request'

// 后台数据统计
export const getStatsSummary = () => request.get('/admin/stats/summary')
export const getHotCourses = (limit = 5) => request.get('/admin/stats/hot-courses', { params: { limit } })
export const getLearnTrend = (days = 7) => request.get('/admin/stats/learn-trend', { params: { days } })
export const getCategoryCourses = () => request.get('/admin/stats/category-courses')
