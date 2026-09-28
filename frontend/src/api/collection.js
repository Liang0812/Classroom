import request from './request'

// 课程收藏
export const collectCourse = (cuid) => request.post('/collections', { cuid })
export const cancelCollection = (cuid) => request.delete(`/collections/${cuid}`)
export const getCollectionStatus = (cuid) => request.get(`/collections/status/${cuid}`)
export const getMyCollections = () => request.get('/collections/mine')
