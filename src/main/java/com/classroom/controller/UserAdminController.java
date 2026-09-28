package com.classroom.controller;

import com.classroom.common.BusinessException;
import com.classroom.common.PageResult;
import com.classroom.common.Result;
import com.classroom.common.ResultCode;
import com.classroom.entity.User;
import com.classroom.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 后台用户管理接口（仅管理员可分配角色，查询管理员/老师可用）
 */
@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    @Autowired
    private UserService userService;

    /** 按角色查询用户列表（如 role=3 学员，消息接收人选择用） */
    @GetMapping
    public Result<List<User>> list(@RequestParam(value = "role", required = false) Integer role,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNum != null && pageSize != null) {
            PageResult<User> page = userService.userPage(pageNum, pageSize, role, keyword);
            return Result.ok(page.getList());
        }
        return Result.ok(userService.listByRole(role));
    }

    /** 用户分页（含角色名） */
    @GetMapping("/page")
    public Result<PageResult<User>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                         @RequestParam(value = "role", required = false) Integer role,
                                         @RequestParam(value = "keyword", required = false) String keyword) {
        return Result.ok(userService.userPage(pageNum, pageSize, role, keyword));
    }

    /** 分配用户角色（仅管理员；不能修改自己的角色） */
    @PutMapping("/{uid}/roles")
    public Result<Void> assignRoles(@PathVariable("uid") Integer uid,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session) {
        checkNotSelf(uid, session);
        Object roleIdsObj = body.get("roleIds");
        List<Integer> roleIds = new ArrayList<>();
        if (roleIdsObj instanceof List) {
            for (Object o : (List<?>) roleIdsObj) {
                roleIds.add(((Number) o).intValue());
            }
        }
        userService.assignRoles(uid, roleIds);
        return Result.ok();
    }

    /** 禁用/启用账号（仅管理员；不能操作自己） */
    @PutMapping("/{uid}/status")
    public Result<Void> updateStatus(@PathVariable("uid") Integer uid,
                                     @RequestBody Map<String, Object> body,
                                     HttpSession session) {
        checkNotSelf(uid, session);
        Integer status = ((Number) body.get("status")).intValue();
        userService.updateStatus(uid, status);
        return Result.ok();
    }

    /** 重置密码（仅管理员） */
    @PutMapping("/{uid}/password")
    public Result<Void> resetPassword(@PathVariable("uid") Integer uid,
                                      @RequestBody Map<String, Object> body) {
        String newPassword = (String) body.get("password");
        userService.resetPassword(uid, newPassword);
        return Result.ok();
    }

    private void checkNotSelf(Integer uid, HttpSession session) {
        Object currentUid = session.getAttribute("loginUserId");
        if (currentUid != null && ((Integer) currentUid) == uid) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不能对自己的账号执行该操作");
        }
    }
}
