package com.classroom.mapper;

import com.classroom.entity.UserQuizResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学员小测答题记录 Mapper
 */
public interface UserQuizResultMapper {

    /** 查询某学员对指定题目的作答记录 */
    List<UserQuizResult> selectByUidQids(@Param("uid") Integer uid, @Param("qids") List<Integer> qids);

    /** 作答（重复作答覆盖：唯一键 UID+QID） */
    int upsert(@Param("uid") Integer uid, @Param("qid") Integer qid,
               @Param("userAnswer") String userAnswer, @Param("isCorrect") Integer isCorrect);
}
