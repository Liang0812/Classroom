package com.classroom.service;

import com.classroom.common.PageResult;
import com.classroom.entity.Course;
import com.classroom.vo.CourseDetailVO;

/**
 * 课程服务
 */
public interface CourseService {

    PageResult<Course> page(int pageNum, int pageSize, String keyword);

    /** 前台课程列表：类别 / 关键字 / 排序（latest、hot）/ 分页 */
    PageResult<Course> portalPage(Integer cid, String keyword, String orderBy, int pageNum, int pageSize);

    /** 前台课程详情：课程 + 章节目录 + 收藏状态（uid 可为空） */
    CourseDetailVO detail(Integer cuid, Integer uid);

    void add(Course course);

    void update(Course course);

    /** 伪删除课程并联动伪删除其章节 */
    void remove(Integer cuid);

    /** 设置推荐状态 */
    void setRecommend(Integer cuid, Integer recommend);
}
