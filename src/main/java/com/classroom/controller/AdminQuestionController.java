package com.classroom.controller;

import com.classroom.common.PageResult;
import com.classroom.common.Result;
import com.classroom.entity.Question;
import com.classroom.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 后台提问管理接口（教师/管理员回复）
 */
@RestController
@RequestMapping("/api/admin/questions")
public class AdminQuestionController {

    @Autowired
    private QuestionService questionService;

    /** 分页查询提问（状态/章节/关键字筛选） */
    @GetMapping
    public Result<PageResult<Question>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                             @RequestParam(value = "status", required = false) Integer status,
                                             @RequestParam(value = "chid", required = false) Integer chid,
                                             @RequestParam(value = "keyword", required = false) String keyword) {
        return Result.ok(questionService.page(pageNum, pageSize, status, chid, keyword));
    }

    /** 回复提问 */
    @PostMapping("/{qid}/reply")
    public Result<Void> reply(@PathVariable("qid") Integer qid, @RequestBody Map<String, String> body) {
        questionService.reply(qid, body.get("answer"));
        return Result.ok();
    }
}
