import request from './request'

// 后台动态菜单
export const getMenus = () => request.get('/admin/menus')

// 角色与节点
export const getRoles = () => request.get('/admin/roles')
export const getNodes = () => request.get('/admin/nodes')
export const getRoleNodes = (rid) => request.get(`/admin/rolenodes/${rid}`)
export const saveRoleNodes = (rid, nids) => request.put(`/admin/rolenodes/${rid}`, { nids })
