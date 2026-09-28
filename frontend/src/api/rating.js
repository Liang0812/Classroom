import request from './request'

// 课程评价（提交需登录；列表/统计已随课程详情接口返回）
export const rateCourse = (data) => request.post('/ratings', data)
