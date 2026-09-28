package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.PageResult;
import com.classroom.common.ResultCode;
import com.classroom.entity.Chapter;
import com.classroom.entity.Course;
import com.classroom.entity.UserCollection;
import com.classroom.mapper.ChapterMapper;
import com.classroom.mapper.CourseMapper;
import com.classroom.mapper.UserCollectionMapper;
import com.classroom.service.CourseRatingService;
import com.classroom.service.CourseService;
import com.classroom.vo.CourseDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 课程服务实现
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private UserCollectionMapper userCollectionMapper;

    @Override
    public PageResult<Course> page(int pageNum, int pageSize, String keyword) {
        int offset = (pageNum - 1) * pageSize;
        List<Course> list = courseMapper.selectList(null, keyword, null, null, offset, pageSize);
        long total = courseMapper.count(null, keyword, null);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public PageResult<Course> portalPage(Integer cid, String keyword, String orderBy, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Course> list = courseMapper.selectList(cid, keyword, null, orderBy, offset, pageSize);
        long total = courseMapper.count(cid, keyword, null);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Autowired
    private CourseRatingService courseRatingService;

    @Override
    public CourseDetailVO detail(Integer cuid, Integer uid) {
        Course course = courseMapper.selectByCuid(cuid);
        if (course == null || course.getStatus() == null || course.getStatus() != 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "课程不存在或已下架");
        }
        List<Chapter> chapters = chapterMapper.selectByCourseId(cuid);
        boolean collected = false;
        if (uid != null) {
            UserCollection uc = userCollectionMapper.selectByUidAndCuid(uid, cuid);
            collected = uc != null && uc.getStatus() != null && uc.getStatus() == 1;
        }
        CourseDetailVO vo = new CourseDetailVO();
        vo.setCourse(course);
        vo.setChapters(chapters);
        vo.setCollected(collected);
        // 评价信息：平均分 + 评价数 + 评价列表 + 我的评分
        java.util.Map<String, Object> stats = courseRatingService.stats(cuid);
        Object avg = stats.get("avgRating");
        Object count = stats.get("ratingCount");
        vo.setAvgRating(avg == null ? null : ((Number) avg).doubleValue());
        vo.setRatingCount(count == null ? 0 : ((Number) count).intValue());
        vo.setRatings(courseRatingService.listByCourse(cuid));
        com.classroom.entity.Rating my = courseRatingService.my(uid, cuid);
        vo.setMyRating(my == null ? null : my.getRating());
        return vo;
    }

    @Override
    @Transactional
    public void add(Course course) {
        if (course == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "课程信息不能为空");
        }
        if (course.getCid() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择课程分类");
        }
        if (!StringUtils.hasText(course.getCourseName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "课程名称不能为空");
        }
        if (!StringUtils.hasText(course.getTeacher())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "授课老师不能为空");
        }
        course.setLearnTime(course.getLearnTime() == null ? 0 : course.getLearnTime());
        course.setClicked(0);
        course.setOrders(course.getOrders() == null ? 0 : course.getOrders());
        course.setRecommend(course.getRecommend() == null ? 0 : course.getRecommend());
        course.setStatus(1);
        courseMapper.insert(course);
    }

    @Override
    public void update(Course course) {
        if (course == null || course.getCuid() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "缺少课程 ID");
        }
        if (!StringUtils.hasText(course.getCourseName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "课程名称不能为空");
        }
        courseMapper.update(course);
    }

    @Override
    @Transactional
    public void remove(Integer cuid) {
        courseMapper.deleteByCuid(cuid);
        chapterMapper.deleteByCourseId(cuid);
    }

    @Override
    public void setRecommend(Integer cuid, Integer recommend) {
        if (cuid == null || recommend == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "参数不完整");
        }
        Course course = new Course();
        course.setCuid(cuid);
        course.setRecommend(recommend);
        courseMapper.update(course);
    }
}
