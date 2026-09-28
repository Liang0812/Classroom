package com.classroom.service.impl;

import com.classroom.entity.Course;
import com.classroom.mapper.CourseMapper;
import com.classroom.service.HomeService;
import com.classroom.vo.HomeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前台首页服务实现：推荐 4 / 最新 4 / 最热 4
 */
@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public HomeVO getHome() {
        HomeVO vo = new HomeVO();
        List<Course> recommend = courseMapper.selectList(null, null, 1, "recommend", 0, 4);
        List<Course> latest = courseMapper.selectList(null, null, null, null, 0, 4);
        List<Course> hottest = courseMapper.selectList(null, null, null, "hot", 0, 4);
        vo.setRecommend(recommend);
        vo.setLatest(latest);
        vo.setHottest(hottest);
        return vo;
    }
}
