package com.classroom.controller;

import com.classroom.common.Result;
import com.classroom.entity.Category;
import com.classroom.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台课程分类接口（匿名可访问，列表筛选用）
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryFrontController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result<List<Category>> list() {
        return Result.ok(categoryService.listAll());
    }
}
