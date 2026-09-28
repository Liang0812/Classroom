package com.classroom.vo;

import com.classroom.entity.Chapter;
import com.classroom.entity.Course;

import java.util.List;

/**
 * 视频播放页数据：当前章节 + 所属课程 + 同课程章节列表
 */
public class PlayInfoVO {

    private Chapter chapter;
    private Course course;
    private List<Chapter> chapters;

    /** 续播位置（秒），来自学习记录 remark 字段 */
    private Double resumePosition;

    public Double getResumePosition() {
        return resumePosition;
    }

    public void setResumePosition(Double resumePosition) {
        this.resumePosition = resumePosition;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }
}
