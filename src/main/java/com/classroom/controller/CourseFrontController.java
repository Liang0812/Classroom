package com.classroom.controller;

import com.classroom.common.PageResult;
import com.classroom.common.Result;
import com.classroom.entity.Course;
import com.classroom.service.CourseService;
import com.classroom.vo.CourseDetailVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台课程接口（匿名可访问：列表 / 详情）
 */
@RestController
@RequestMapping("/api/courses")
public class CourseFrontController {

    @Autowired
    private CourseService courseService;

    /** 课程列表：类别 / 关键字 / 排序（latest、hot）/ 分页 */
    @GetMapping
    public Result<PageResult<Course>> list(@RequestParam(value = "cid", required = false) Integer cid,
                                           @RequestParam(value = "keyword", required = false) String keyword,
                                           @RequestParam(value = "orderBy", required = false) String orderBy,
                                           @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "9") int pageSize) {
        return Result.ok(courseService.portalPage(cid, keyword, orderBy, pageNum, pageSize));
    }

    /** 课程详情：课程 + 章节目录 + 收藏状态 */
    @GetMapping("/{cuid}")
    public Result<CourseDetailVO> detail(@PathVariable("cuid") Integer cuid, HttpSession session) {
        Object uid = session.getAttribute("loginUserId");
        Integer loginUid = uid == null ? null : (Integer) uid;
        return Result.ok(courseService.detail(cuid, loginUid));
    }
}
