package com.classroom.mapper;

import com.classroom.entity.ChapterQuiz;

import java.util.List;

/**
 * 章节小测 Mapper
 */
public interface ChapterQuizMapper {

    List<ChapterQuiz> selectByChid(Integer chid);

    int insert(ChapterQuiz quiz);

    int update(ChapterQuiz quiz);

    int deleteByQid(Integer qid);
}
