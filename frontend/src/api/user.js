import request from './request'

// 后台用户管理
export const getUsersPage = (params) => request.get('/admin/users/page', { params })
export const assignUserRoles = (uid, roleIds) => request.put(`/admin/users/${uid}/roles`, { roleIds })
export const updateUserStatus = (uid, status) => request.put(`/admin/users/${uid}/status`, { status })
export const resetUserPassword = (uid, password) => request.put(`/admin/users/${uid}/password`, { password })
