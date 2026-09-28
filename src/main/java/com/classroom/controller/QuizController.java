package com.classroom.controller;

import com.classroom.common.Result;
import com.classroom.common.ResultCode;
import com.classroom.entity.ChapterQuiz;
import com.classroom.service.ChapterQuizService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 学员端章节小测接口（需登录）
 */
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private ChapterQuizService chapterQuizService;

    /** 取某章节的小测题目（隐藏答案，附带本人已答记录） */
    @GetMapping("/chapter/{chid}")
    public Result<List<ChapterQuiz>> chapterQuiz(@PathVariable("chid") Integer chid, HttpSession session) {
        Integer uid = requireLogin(session);
        return Result.ok(chapterQuizService.quizForStudent(chid, uid));
    }

    /** 提交作答：返回 {total, correct, details} */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submit(@RequestBody Map<String, Object> body, HttpSession session) {
        Integer uid = requireLogin(session);
        Integer chid = ((Number) body.get("chid")).intValue();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> answers = (List<Map<String, Object>>) body.get("answers");
        return Result.ok(chapterQuizService.submit(uid, chid, answers));
    }

    private Integer requireLogin(HttpSession session) {
        Object uid = session.getAttribute("loginUserId");
        if (uid == null) {
            throw new com.classroom.common.BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        return (Integer) uid;
    }
}
