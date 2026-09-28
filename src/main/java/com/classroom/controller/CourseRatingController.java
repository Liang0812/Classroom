package com.classroom.controller;

import com.classroom.common.BusinessException;
import com.classroom.common.Result;
import com.classroom.common.ResultCode;
import com.classroom.service.CourseRatingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 课程评价接口（需登录）
 */
@RestController
@RequestMapping("/api/ratings")
public class CourseRatingController {

    @Autowired
    private CourseRatingService courseRatingService;

    /** 提交/更新评价（每学员每课程一条，重复提交为更新） */
    @PostMapping
    public Result<Void> rate(@RequestBody Map<String, Object> body, HttpSession session) {
        Object uid = session.getAttribute("loginUserId");
        if (uid == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        Object cuid = body.get("cuid");
        Object rating = body.get("rating");
        if (cuid == null || rating == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "参数不完整");
        }
        Object comment = body.get("comment");
        courseRatingService.rate((Integer) uid, ((Number) cuid).intValue(), ((Number) rating).intValue(),
                comment == null ? null : comment.toString());
        return Result.ok();
    }
}
