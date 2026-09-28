package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.ResultCode;
import com.classroom.entity.Course;
import com.classroom.entity.Rating;
import com.classroom.mapper.CourseMapper;
import com.classroom.mapper.CourseRatingMapper;
import com.classroom.service.CourseRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 课程评价服务实现
 */
@Service
public class CourseRatingServiceImpl implements CourseRatingService {

    @Autowired
    private CourseRatingMapper courseRatingMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    @Transactional
    public void rate(Integer uid, Integer cuid, Integer rating, String comment) {
        if (cuid == null || rating == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "参数不完整");
        }
        if (rating < 1 || rating > 5) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "评分需在 1-5 星之间");
        }
        Course course = courseMapper.selectByCuid(cuid);
        if (course == null || course.getStatus() == null || course.getStatus() != 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "课程不存在或已下架");
        }
        Rating r = new Rating();
        r.setCuid(cuid);
        r.setUid(uid);
        r.setRating(rating);
        r.setComment(comment != null && !comment.trim().isEmpty() ? comment.trim() : null);
        r.setStatus(1);
        courseRatingMapper.insertOrUpdate(r);
    }

    @Override
    public List<Rating> listByCourse(Integer cuid) {
        return courseRatingMapper.selectByCourse(cuid);
    }

    @Override
    public Map<String, Object> stats(Integer cuid) {
        return courseRatingMapper.selectStatsByCuid(cuid);
    }

    @Override
    public Rating my(Integer uid, Integer cuid) {
        if (uid == null) {
            return null;
        }
        return courseRatingMapper.selectByCuidAndUid(cuid, uid);
    }
}
