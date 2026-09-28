package com.classroom.controller;

import com.classroom.common.PageResult;
import com.classroom.common.Result;
import com.classroom.entity.Course;
import com.classroom.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台课程管理接口
 */
@RestController
@RequestMapping("/api/admin/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /** 课程分页列表（可按课程名模糊查询） */
    @GetMapping
    public Result<PageResult<Course>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                           @RequestParam(value = "keyword", required = false) String keyword) {
        return Result.ok(courseService.page(pageNum, pageSize, keyword));
    }

    /** 添加课程 */
    @PostMapping
    public Result<Void> add(@RequestBody Course course) {
        courseService.add(course);
        return Result.ok();
    }

    /** 修改课程 */
    @PutMapping("/{cuid}")
    public Result<Void> update(@PathVariable("cuid") Integer cuid, @RequestBody Course course) {
        course.setCuid(cuid);
        courseService.update(course);
        return Result.ok();
    }

    /** 删除课程（伪删除，联动伪删除章节，可回收站恢复） */
    @DeleteMapping("/{cuid}")
    public Result<Void> remove(@PathVariable("cuid") Integer cuid) {
        courseService.remove(cuid);
        return Result.ok();
    }

    /** 设置/取消推荐 */
    @PutMapping("/{cuid}/recommend")
    public Result<Void> recommend(@PathVariable("cuid") Integer cuid, @RequestParam(value = "recommend") Integer recommend) {
        courseService.setRecommend(cuid, recommend);
        return Result.ok();
    }
}
