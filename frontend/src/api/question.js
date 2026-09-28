import request from './request'

// 学员端问答
export const getChapterQuestions = (chid) => request.get(`/questions/chapter/${chid}`)
export const askQuestion = (chid, question) => request.post('/questions', { chid, question })
export const getMyQuestions = () => request.get('/questions/mine')

// 后台提问管理
export const getQuestionPage = (params) => request.get('/admin/questions', { params })
export const replyQuestion = (qid, answer) => request.post(`/admin/questions/${qid}/reply`, { answer })
