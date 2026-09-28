package com.classroom.controller;

import com.classroom.common.BusinessException;
import com.classroom.common.Result;
import com.classroom.common.ResultCode;
import com.classroom.entity.Node;
import com.classroom.mapper.NodeMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台动态菜单接口：按当前用户角色返回菜单节点
 */
@RestController
@RequestMapping("/api/admin/menus")
public class MenuController {

    /** 系统管理员角色 ID */
    private static final int ROLE_ADMIN = 1;
    /** 老师角色 ID */
    private static final int ROLE_TEACHER = 2;

    @Autowired
    private NodeMapper nodeMapper;

    @GetMapping
    public Result<List<Node>> menus(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Integer> roleIds = (List<Integer>) session.getAttribute("loginRoleIds");
        if (roleIds == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        if (contains(roleIds, ROLE_ADMIN)) {
            return Result.ok(nodeMapper.selectAll());
        }
        if (contains(roleIds, ROLE_TEACHER)) {
            return Result.ok(nodeMapper.selectByRoleId(ROLE_TEACHER));
        }
        throw new BusinessException(ResultCode.FORBIDDEN, "无权访问后台");
    }

    private boolean contains(List<Integer> roleIds, int roleId) {
        for (Integer id : roleIds) {
            if (id != null && id == roleId) {
                return true;
            }
        }
        return false;
    }
}
