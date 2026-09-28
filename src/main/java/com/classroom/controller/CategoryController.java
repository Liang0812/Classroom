package com.classroom.controller;

import com.classroom.common.PageResult;
import com.classroom.common.Result;
import com.classroom.entity.Category;
import com.classroom.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台课程分类管理接口
 */
@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /** 分类分页列表（可按名称模糊查询） */
    @GetMapping
    public Result<PageResult<Category>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                             @RequestParam(value = "keyword", required = false) String keyword) {
        return Result.ok(categoryService.page(pageNum, pageSize, keyword));
    }

    /** 全部正常分类（表单下拉用） */
    @GetMapping("/all")
    public Result<List<Category>> all() {
        return Result.ok(categoryService.listAll());
    }

    /** 添加分类 */
    @PostMapping
    public Result<Void> add(@RequestBody Category category) {
        categoryService.add(category);
        return Result.ok();
    }

    /** 修改分类 */
    @PutMapping("/{cid}")
    public Result<Void> update(@PathVariable("cid") Integer cid, @RequestBody Category category) {
        category.setCid(cid);
        categoryService.update(category);
        return Result.ok();
    }

    /** 删除分类（伪删除，可回收站恢复） */
    @DeleteMapping("/{cid}")
    public Result<Void> remove(@PathVariable("cid") Integer cid) {
        categoryService.remove(cid);
        return Result.ok();
    }
}
