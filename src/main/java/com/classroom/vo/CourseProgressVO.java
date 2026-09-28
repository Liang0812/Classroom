package com.classroom.vo;

/**
 * 我的课程学习进度
 */
public class CourseProgressVO {

    private Integer cuid;
    private String courseName;
    private String teacher;
    private Integer totalChapters;
    private Integer learnedChapters;
    private Integer progress;
    private Integer lastChapterId;
    private String lastChapterName;
    private java.util.Date finishTime;

    public Integer getLastChapterId() {
        return lastChapterId;
    }

    public void setLastChapterId(Integer lastChapterId) {
        this.lastChapterId = lastChapterId;
    }

    public String getLastChapterName() {
        return lastChapterName;
    }

    public void setLastChapterName(String lastChapterName) {
        this.lastChapterName = lastChapterName;
    }

    public java.util.Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(java.util.Date finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getCuid() {
        return cuid;
    }

    public void setCuid(Integer cuid) {
        this.cuid = cuid;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public Integer getTotalChapters() {
        return totalChapters;
    }

    public void setTotalChapters(Integer totalChapters) {
        this.totalChapters = totalChapters;
    }

    public Integer getLearnedChapters() {
        return learnedChapters;
    }

    public void setLearnedChapters(Integer learnedChapters) {
        this.learnedChapters = learnedChapters;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
