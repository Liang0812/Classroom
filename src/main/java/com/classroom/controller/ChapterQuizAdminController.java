package com.classroom.controller;

import com.classroom.common.Result;
import com.classroom.entity.ChapterQuiz;
import com.classroom.service.ChapterQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 章节小测管理接口（管理员/教师）
 */
@RestController
@RequestMapping("/api/admin/chapters")
public class ChapterQuizAdminController {

    @Autowired
    private ChapterQuizService chapterQuizService;

    /** 某章节的全部题目（含答案） */
    @GetMapping("/{chid}/quizzes")
    public Result<List<ChapterQuiz>> listByChid(@PathVariable("chid") Integer chid) {
        return Result.ok(chapterQuizService.listByChid(chid));
    }

    /** 添加题目 */
    @PostMapping("/{chid}/quizzes")
    public Result<Void> add(@PathVariable("chid") Integer chid, @RequestBody ChapterQuiz quiz) {
        chapterQuizService.add(chid, quiz);
        return Result.ok();
    }

    /** 修改题目 */
    @PutMapping("/quizzes/{qid}")
    public Result<Void> update(@PathVariable("qid") Integer qid, @RequestBody ChapterQuiz quiz) {
        quiz.setQid(qid);
        chapterQuizService.update(quiz);
        return Result.ok();
    }

    /** 删除题目 */
    @DeleteMapping("/quizzes/{qid}")
    public Result<Void> remove(@PathVariable("qid") Integer qid) {
        chapterQuizService.delete(qid);
        return Result.ok();
    }
}
