package com.classroom.mapper;

import com.classroom.entity.ChapterFile;

import java.util.List;

/**
 * 章节文件 Mapper
 */
public interface ChapterFileMapper {

    List<ChapterFile> selectByChid(Integer chid);

    int insert(ChapterFile file);

    int deleteByFileId(Integer fileId);
}
