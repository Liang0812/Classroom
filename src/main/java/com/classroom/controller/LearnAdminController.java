package com.classroom.controller;

import com.classroom.common.PageResult;
import com.classroom.common.Result;
import com.classroom.entity.UserLearn;
import com.classroom.mapper.UserLearnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台学习记录管理接口（管理员/老师）
 */
@RestController
@RequestMapping("/api/admin/learns")
public class LearnAdminController {

    @Autowired
    private UserLearnMapper userLearnMapper;

    @GetMapping
    public Result<PageResult<UserLearn>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<UserLearn> list = userLearnMapper.selectAdminPage(offset, pageSize);
        long total = userLearnMapper.countAdmin();
        return Result.ok(new PageResult<>(list, total, pageNum, pageSize));
    }
}
