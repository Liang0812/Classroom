import request from './request'

// 学习记录 / 视频点播
export const getPlayInfo = (chid) => request.get(`/learn/chapter/${chid}`)
export const startLearn = (chid) => request.post('/learn/start', null, { params: { chid } })
export const endLearn = (chid) => request.post('/learn/end', null, { params: { chid } })
export const savePosition = (chid, position) => request.post('/learn/position', { chid, position })
export const getLearnProgress = () => request.get('/learn/progress')

// 后台学习记录管理
export const getLearnAdminPage = (params) => request.get('/admin/learns', { params })
