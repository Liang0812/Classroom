package com.classroom.service;

import com.classroom.entity.Rating;

import java.util.List;
import java.util.Map;

/**
 * 课程评价服务
 */
public interface CourseRatingService {

    /** 提交/更新评价（每学员每课程一条） */
    void rate(Integer uid, Integer cuid, Integer rating, String comment);

    /** 课程评价列表（联查学员姓名） */
    List<Rating> listByCourse(Integer cuid);

    /** 课程评分统计：avgRating + ratingCount */
    Map<String, Object> stats(Integer cuid);

    /** 当前学员对某课程的评分（未评返回 null） */
    Rating my(Integer uid, Integer cuid);
}
