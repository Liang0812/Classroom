package com.classroom.controller;

import com.classroom.common.Result;
import com.classroom.common.ResultCode;
import com.classroom.entity.UserCollection;
import com.classroom.service.UserCollectionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 课程收藏接口（需登录）
 */
@RestController
@RequestMapping("/api/collections")
public class UserCollectionController {

    @Autowired
    private UserCollectionService userCollectionService;

    /** 收藏课程 */
    @PostMapping
    public Result<Void> collect(@RequestBody Map<String, Integer> body, HttpSession session) {
        Integer uid = requireUid(session);
        userCollectionService.collect(uid, body.get("cuid"));
        return Result.ok();
    }

    /** 取消收藏 */
    @DeleteMapping("/{cuid}")
    public Result<Void> cancel(@PathVariable("cuid") Integer cuid, HttpSession session) {
        Integer uid = requireUid(session);
        userCollectionService.cancel(uid, cuid);
        return Result.ok();
    }

    /** 当前用户是否已收藏 */
    @GetMapping("/status/{cuid}")
    public Result<Boolean> status(@PathVariable("cuid") Integer cuid, HttpSession session) {
        Integer uid = requireUid(session);
        return Result.ok(userCollectionService.isCollected(uid, cuid));
    }

    /** 我的收藏列表（联查课程信息） */
    @GetMapping("/mine")
    public Result<List<UserCollection>> mine(HttpSession session) {
        Integer uid = requireUid(session);
        return Result.ok(userCollectionService.mine(uid));
    }

    private Integer requireUid(HttpSession session) {
        Object uid = session.getAttribute("loginUserId");
        if (uid == null) {
            throw new com.classroom.common.BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        return (Integer) uid;
    }
}
