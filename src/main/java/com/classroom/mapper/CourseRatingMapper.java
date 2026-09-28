package com.classroom.mapper;

import com.classroom.entity.Rating;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 课程评价 Mapper
 */
public interface CourseRatingMapper {

    /** 某学员对某课程的评分 */
    Rating selectByCuidAndUid(@Param("cuid") Integer cuid, @Param("uid") Integer uid);

    /** 新增或更新评价（每学员每课程一条） */
    int insertOrUpdate(Rating rating);

    /** 课程评价列表（联查学员姓名） */
    List<Rating> selectByCourse(@Param("cuid") Integer cuid);

    /** 课程评分统计：平均分 + 评价数 */
    Map<String, Object> selectStatsByCuid(@Param("cuid") Integer cuid);
}
