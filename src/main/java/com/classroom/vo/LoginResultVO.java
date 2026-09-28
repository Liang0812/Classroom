package com.classroom.vo;

import java.util.List;

/**
 * 登录结果：用户信息 + 角色列表
 */
public class LoginResultVO {

    private UserVO user;
    private List<String> roles;
    /** 角色 ID 列表（权限校验用） */
    private List<Integer> roleIds;

    public LoginResultVO() {
    }

    public LoginResultVO(UserVO user, List<String> roles, List<Integer> roleIds) {
        this.user = user;
        this.roles = roles;
        this.roleIds = roleIds;
    }

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
