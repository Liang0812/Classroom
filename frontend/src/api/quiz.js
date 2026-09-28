import request from './request'

// ===== 学员端小测 =====
export const getChapterQuiz = (chid) => request.get(`/quiz/chapter/${chid}`)
export const submitQuiz = (data) => request.post('/quiz/submit', data)

// ===== 管理端小测（管理员/教师） =====
export const getAdminQuizzes = (chid) => request.get(`/admin/chapters/${chid}/quizzes`)
export const addAdminQuiz = (chid, data) => request.post(`/admin/chapters/${chid}/quizzes`, data)
export const updateAdminQuiz = (qid, data) => request.put(`/admin/chapters/quizzes/${qid}`, data)
export const deleteAdminQuiz = (qid) => request.delete(`/admin/chapters/quizzes/${qid}`)
