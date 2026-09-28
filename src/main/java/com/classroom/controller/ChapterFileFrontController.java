package com.classroom.controller;

import com.classroom.common.Result;
import com.classroom.entity.ChapterFile;
import com.classroom.service.ChapterFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台章节文件接口（匿名可访问：课程详情页查看章节课件）
 */
@RestController
@RequestMapping("/api/chapter-files")
public class ChapterFileFrontController {

    @Autowired
    private ChapterFileService chapterFileService;

    /** 某章节的文件列表 */
    @GetMapping("/{chid}")
    public Result<List<ChapterFile>> listByChid(@PathVariable("chid") Integer chid) {
        return Result.ok(chapterFileService.listByChid(chid));
    }
}
