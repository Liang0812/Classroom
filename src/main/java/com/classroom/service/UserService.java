package com.classroom.service;

import com.classroom.common.PageResult;
import com.classroom.entity.User;
import com.classroom.vo.LoginResultVO;
import com.classroom.vo.LoginVO;
import com.classroom.vo.RegisterVO;
import com.classroom.vo.UserVO;

import java.util.List;

/**
 * 用户服务：注册 / 登录 / 退出 / 当前用户
 */
public interface UserService {

    UserVO register(RegisterVO registerVO);

    LoginResultVO login(LoginVO loginVO);

    LoginResultVO currentUser(Integer uid);

    /** 按角色查询用户列表 */
    List<User> listByRole(Integer roleId);

    /** 后台用户分页（角色/关键字筛选，含角色名） */
    PageResult<User> userPage(int pageNum, int pageSize, Integer role, String keyword);

    /** 分配用户角色（先清后插） */
    void assignRoles(Integer uid, List<Integer> roleIds);

    /** 禁用/启用账号（仅管理员） */
    void updateStatus(Integer uid, Integer status);

    /** 重置密码（仅管理员） */
    void resetPassword(Integer uid, String newPassword);

    /** 修改密码（校验旧密码） */
    void changePassword(Integer uid, String oldPassword, String newPassword);

    /** 更新用户头像（avatar 为 /upload/avatar/... 访问地址） */
    void updateAvatar(Integer uid, String avatar);
}
