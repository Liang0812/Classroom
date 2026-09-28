package com.classroom.controller;

import com.classroom.common.Result;
import com.classroom.entity.Chapter;
import com.classroom.service.ChapterService;
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
 * 后台课程章节管理接口
 */
@RestController
@RequestMapping("/api/admin/chapters")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    /** 某课程的章节列表 */
    @GetMapping("/course/{cuid}")
    public Result<List<Chapter>> listByCourse(@PathVariable("cuid") Integer cuid) {
        return Result.ok(chapterService.listByCourse(cuid));
    }

    /** 添加章节 */
    @PostMapping
    public Result<Void> add(@RequestBody Chapter chapter) {
        chapterService.add(chapter);
        return Result.ok();
    }

    /** 修改章节 */
    @PutMapping("/{chid}")
    public Result<Void> update(@PathVariable("chid") Integer chid, @RequestBody Chapter chapter) {
        chapter.setChid(chid);
        chapterService.update(chapter);
        return Result.ok();
    }

    /** 删除章节（伪删除，可回收站恢复） */
    @DeleteMapping("/{chid}")
    public Result<Void> remove(@PathVariable("chid") Integer chid) {
        chapterService.remove(chid);
        return Result.ok();
    }
}
