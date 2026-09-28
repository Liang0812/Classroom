package com.classroom.vo;

import com.classroom.entity.Course;

import java.util.List;

/**
 * 前台首页数据：推荐 / 最新 / 最热 课程
 */
public class HomeVO {

    /** 推荐课程（前 4，含封面，作为 Banner 轮播与推荐区数据） */
    private List<Course> recommend;

    /** 最新课程（前 4） */
    private List<Course> latest;

    /** 最热课程（前 4，按点击量） */
    private List<Course> hottest;

    public List<Course> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<Course> recommend) {
        this.recommend = recommend;
    }

    public List<Course> getLatest() {
        return latest;
    }

    public void setLatest(List<Course> latest) {
        this.latest = latest;
    }

    public List<Course> getHottest() {
        return hottest;
    }

    public void setHottest(List<Course> hottest) {
        this.hottest = hottest;
    }
}
