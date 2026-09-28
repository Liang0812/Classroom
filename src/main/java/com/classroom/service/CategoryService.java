package com.classroom.service;

import com.classroom.common.PageResult;
import com.classroom.entity.Category;

import java.util.List;

/**
 * 课程分类服务
 */
public interface CategoryService {

    PageResult<Category> page(int pageNum, int pageSize, String keyword);

    List<Category> listAll();

    void add(Category category);

    void update(Category category);

    void remove(Integer cid);
}
