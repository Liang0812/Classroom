package com.classroom.controller;

import com.classroom.common.Result;
import com.classroom.entity.Category;
import com.classroom.entity.Chapter;
import com.classroom.entity.Course;
import com.classroom.entity.Question;
import com.classroom.service.RecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 回收站接口（管理员/老师）：
 * 伪删除数据的查看、恢复、彻底删除（彻底删除含关联数据清理）
 */
@RestController
@RequestMapping("/api/admin/recycle")
public class RecycleAdminController {

    @Autowired
    private RecycleService recycleService;

    /* ---------- 分类 ---------- */

    @GetMapping("/categories")
    public Result<List<Category>> deletedCategories() {
        return Result.ok(recycleService.deletedCategories());
    }

    @PostMapping("/categories/{cid}/restore")
    public Result<Void> restoreCategory(@PathVariable("cid") Integer cid) {
        recycleService.restoreCategory(cid);
        return Result.ok();
    }

    @DeleteMapping("/categories/{cid}")
    public Result<Void> deleteCategory(@PathVariable("cid") Integer cid) {
        recycleService.deleteCategory(cid);
        return Result.ok();
    }

    /* ---------- 课程 ---------- */

    @GetMapping("/courses")
    public Result<List<Course>> deletedCourses() {
        return Result.ok(recycleService.deletedCourses());
    }

    @PostMapping("/courses/{cuid}/restore")
    public Result<Void> restoreCourse(@PathVariable("cuid") Integer cuid) {
        recycleService.restoreCourse(cuid);
        return Result.ok();
    }

    @DeleteMapping("/courses/{cuid}")
    public Result<Void> deleteCourse(@PathVariable("cuid") Integer cuid) {
        recycleService.deleteCourse(cuid);
        return Result.ok();
    }

    /* ---------- 章节 ---------- */

    @GetMapping("/chapters")
    public Result<List<Chapter>> deletedChapters() {
        return Result.ok(recycleService.deletedChapters());
    }

    @PostMapping("/chapters/{chid}/restore")
    public Result<Void> restoreChapter(@PathVariable("chid") Integer chid) {
        recycleService.restoreChapter(chid);
        return Result.ok();
    }

    @DeleteMapping("/chapters/{chid}")
    public Result<Void> deleteChapter(@PathVariable("chid") Integer chid) {
        recycleService.deleteChapter(chid);
        return Result.ok();
    }

    /* ---------- 提问 ---------- */

    @GetMapping("/questions")
    public Result<List<Question>> deletedQuestions() {
        return Result.ok(recycleService.deletedQuestions());
    }

    @PostMapping("/questions/{qid}/restore")
    public Result<Void> restoreQuestion(@PathVariable("qid") Integer qid) {
        recycleService.restoreQuestion(qid);
        return Result.ok();
    }

    @DeleteMapping("/questions/{qid}")
    public Result<Void> deleteQuestion(@PathVariable("qid") Integer qid) {
        recycleService.deleteQuestion(qid);
        return Result.ok();
    }
}
