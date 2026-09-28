package com.classroom.controller;

import com.classroom.common.Result;
import com.classroom.service.HomeService;
import com.classroom.vo.HomeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台首页接口（匿名可访问）
 */
@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping
    public Result<HomeVO> home() {
        return Result.ok(homeService.getHome());
    }
}
