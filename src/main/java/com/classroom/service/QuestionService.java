package com.classroom.service;

import com.classroom.common.PageResult;
import com.classroom.entity.Question;

import java.util.List;

/**
 * 问答服务
 */
public interface QuestionService {

    /** 本章提问列表 */
    List<Question> listByChapter(Integer chid);

    /** 学员提问 */
    void ask(Integer uid, Integer chid, String question);

    /** 我的提问列表 */
    List<Question> myQuestions(Integer uid);

    /** 后台分页 */
    PageResult<Question> page(int pageNum, int pageSize, Integer status, Integer chid, String keyword);

    /** 教师/管理员回复 */
    void reply(Integer qid, String answer);
}
