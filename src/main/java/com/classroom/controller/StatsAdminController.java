package com.classroom.controller;

import com.classroom.common.Result;
import com.classroom.entity.Course;
import com.classroom.mapper.StatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 后台数据统计接口（管理员/老师）
 */
@RestController
@RequestMapping("/api/admin/stats")
public class StatsAdminController {

    @Autowired
    private StatsMapper statsMapper;

    /** 总览指标 */
    @GetMapping("/summary")
    public Result<Map<String, Object>> summary() {
        return Result.ok(statsMapper.selectSummary());
    }

    /** 热门课程 TOP N（默认 5） */
    @GetMapping("/hot-courses")
    public Result<List<Course>> hotCourses(@RequestParam(value = "limit", defaultValue = "5") int limit) {
        return Result.ok(statsMapper.selectHotCourses(limit));
    }

    /** 近 N 天学习记录趋势（默认 7） */
    @GetMapping("/learn-trend")
    public Result<List<Map<String, Object>>> learnTrend(@RequestParam(value = "days", defaultValue = "7") int days) {
        return Result.ok(statsMapper.selectLearnTrend(days));
    }

    /** 各分类课程数分布 */
    @GetMapping("/category-courses")
    public Result<List<Map<String, Object>>> categoryCourses() {
        return Result.ok(statsMapper.selectCategoryCourses());
    }
}
