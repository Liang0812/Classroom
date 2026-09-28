package com.classroom.vo;

import com.classroom.entity.Chapter;
import com.classroom.entity.Course;
import com.classroom.entity.Rating;

import java.util.List;

/**
 * 课程详情：课程信息 + 章节目录 + 收藏状态 + 评价信息
 */
public class CourseDetailVO {

    private Course course;
    private List<Chapter> chapters;
    private boolean collected;
    private Double avgRating;
    private Integer ratingCount;
    private Integer myRating;
    private List<Rating> ratings;

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Integer getMyRating() {
        return myRating;
    }

    public void setMyRating(Integer myRating) {
        this.myRating = myRating;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
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

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}
