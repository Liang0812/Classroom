package com.classroom.service;

import com.classroom.entity.Chapter;

import java.util.List;

/**
 * 课程章节服务
 */
public interface ChapterService {

    List<Chapter> listByCourse(Integer cuid);

    void add(Chapter chapter);

    void update(Chapter chapter);

    /** 伪删除章节 */
    void remove(Integer chid);
}
