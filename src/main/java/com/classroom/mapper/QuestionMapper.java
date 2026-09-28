package com.classroom.mapper;

import com.classroom.entity.Question;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问答表 Mapper
 */
public interface QuestionMapper {

    Question selectByQid(@Param("qid") Integer qid);

    /** 本章提问列表（联查提问人/章节/课程名） */
    List<Question> selectByChid(@Param("chid") Integer chid);

    /** 我的提问列表（联查章节/课程名） */
    List<Question> selectByUid(@Param("uid") Integer uid);

    /** 后台分页（状态/章节/关键字筛选） */
    List<Question> selectPage(@Param("offset") int offset,
                              @Param("limit") int limit,
                              @Param("status") Integer status,
                              @Param("chid") Integer chid,
                              @Param("keyword") String keyword);

    long count(@Param("status") Integer status,
               @Param("chid") Integer chid,
               @Param("keyword") String keyword);

    int insert(Question question);

    /** 回复：写入回答并将状态置为 2 已回答 */
    int reply(@Param("qid") Integer qid, @Param("answer") String answer);

    int update(Question question);

    /** 伪删除：状态置 0 */
    int deleteByQid(@Param("qid") Integer qid);

    /** 回收站：伪删除提问列表 */
    List<Question> selectDeleted();

    /** 恢复：状态置 1 */
    int restore(@Param("qid") Integer qid);

    /** 彻底删除（物理） */
    int deleteByQidPhysical(@Param("qid") Integer qid);

    /** 按章节 ID 集合物理删除（章节彻底删除前联动清理） */
    int deleteByChids(@Param("chids") List<Integer> chids);
}
