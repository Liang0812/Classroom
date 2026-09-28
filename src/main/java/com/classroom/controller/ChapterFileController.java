package com.classroom.controller;

import com.classroom.common.Result;
import com.classroom.entity.ChapterFile;
import com.classroom.service.ChapterFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 章节文件管理接口（管理员/教师，经 AdminPermissionInterceptor 鉴权）
 */
@RestController
@RequestMapping("/api/admin/chapters")
public class ChapterFileController {

    @Autowired
    private ChapterFileService chapterFileService;

    /** 某章节的文件列表 */
    @GetMapping("/{chid}/files")
    public Result<List<ChapterFile>> listByChid(@PathVariable("chid") Integer chid) {
        return Result.ok(chapterFileService.listByChid(chid));
    }

    /** 绑定文件到章节 */
    @PostMapping("/{chid}/files")
    public Result<Void> add(@PathVariable("chid") Integer chid, @RequestBody ChapterFile file) {
        chapterFileService.add(chid, file);
        return Result.ok();
    }

    /** 删除文件记录 */
    @DeleteMapping("/files/{fileId}")
    public Result<Void> remove(@PathVariable("fileId") Integer fileId) {
        chapterFileService.delete(fileId);
        return Result.ok();
    }
}
