import request from './request'

// 后台分类管理
export const getCategoryPage = (params) => request.get('/admin/categories', { params })
export const getAllCategories = () => request.get('/admin/categories/all')
export const addCategory = (data) => request.post('/admin/categories', data)
export const updateCategory = (cid, data) => request.put(`/admin/categories/${cid}`, data)
export const deleteCategory = (cid) => request.delete(`/admin/categories/${cid}`)

// 前台分类（匿名）
export const getFrontCategories = () => request.get('/categories')
