package com.classroom.service;

import com.classroom.entity.ChapterFile;

import java.util.List;

/**
 * 章节文件服务
 */
public interface ChapterFileService {

    /** 某章节的文件列表 */
    List<ChapterFile> listByChid(Integer chid);

    /** 绑定文件到章节 */
    void add(Integer chid, ChapterFile file);

    /** 删除文件记录 */
    void delete(Integer fileId);
}
