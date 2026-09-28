package com.classroom.service;

import com.classroom.entity.ChapterQuiz;

import java.util.List;
import java.util.Map;

/**
 * 章节小测服务
 */
public interface ChapterQuizService {

    /** 管理端：某章节全部题目（含答案） */
    List<ChapterQuiz> listByChid(Integer chid);

    /** 添加题目（校验选项与答案） */
    void add(Integer chid, ChapterQuiz quiz);

    /** 修改题目 */
    void update(ChapterQuiz quiz);

    /** 删除题目 */
    void delete(Integer qid);

    /** 学员端：取题（去答案，附带本人已答记录） */
    List<ChapterQuiz> quizForStudent(Integer chid, Integer uid);

    /** 学员提交作答：判分并记录，返回 {total, correct, details} */
    Map<String, Object> submit(Integer uid, Integer chid, List<Map<String, Object>> answers);
}
