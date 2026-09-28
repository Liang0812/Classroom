package com.classroom.mapper;

import com.classroom.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 数据统计 Mapper（后台首页）
 */
public interface StatsMapper {

    /** 总览指标：用户/课程/分类/学习记录/提问/消息 */
    Map<String, Object> selectSummary();

    /** 热门课程 TOP N */
    List<Course> selectHotCourses(@Param("limit") int limit);

    /** 近 N 天学习记录按日趋势 */
    List<Map<String, Object>> selectLearnTrend(@Param("days") int days);

    /** 各分类课程数分布 */
    List<Map<String, Object>> selectCategoryCourses();
}
