package com.classroom.controller;

import com.classroom.common.BusinessException;
import com.classroom.common.Result;
import com.classroom.common.ResultCode;
import com.classroom.entity.Question;
import com.classroom.service.QuestionService;
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
 * 学员端问答接口（需登录）
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /** 本章提问列表 */
    @GetMapping("/chapter/{chid}")
    public Result<List<Question>> listByChapter(@PathVariable("chid") Integer chid) {
        return Result.ok(questionService.listByChapter(chid));
    }

    /** 学员提问 */
    @PostMapping
    public Result<Void> ask(@RequestBody Map<String, Object> body, HttpSession session) {
        Integer uid = requireUid(session);
        Object chidObj = body.get("chid");
        Integer chid = chidObj == null ? null : ((Number) chidObj).intValue();
        String question = body.get("question") == null ? null : body.get("question").toString();
        questionService.ask(uid, chid, question);
        return Result.ok();
    }

    /** 我的提问列表 */
    @GetMapping("/mine")
    public Result<List<Question>> mine(HttpSession session) {
        return Result.ok(questionService.myQuestions(requireUid(session)));
    }

    private Integer requireUid(HttpSession session) {
        Object uid = session.getAttribute("loginUserId");
        if (uid == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        return (Integer) uid;
    }
}
