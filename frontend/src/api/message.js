import request from './request'

// 学员端消息
export const getMyMessages = (params) => request.get('/messages', { params })
export const getUnreadCount = () => request.get('/messages/unread-count')
export const getMessageDetail = (mid) => request.get(`/messages/${mid}`)

// 后台消息管理
export const getMessagePage = (params) => request.get('/admin/messages', { params })
export const sendMessage = (data) => request.post('/admin/messages', data)
