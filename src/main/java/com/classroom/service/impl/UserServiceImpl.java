package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.PageResult;
import com.classroom.common.ResultCode;
import com.classroom.entity.Role;
import com.classroom.entity.User;
import com.classroom.entity.UserRole;
import com.classroom.mapper.UserMapper;
import com.classroom.mapper.UserRoleMapper;
import com.classroom.service.UserService;
import com.classroom.util.MD5Util;
import com.classroom.vo.LoginResultVO;
import com.classroom.vo.LoginVO;
import com.classroom.vo.RegisterVO;
import com.classroom.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    /** 新注册用户默认角色：学员（roles 表现有数据 rid=3） */
    private static final int DEFAULT_ROLE_ID = 3;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    @Transactional
    public UserVO register(RegisterVO vo) {
        if (!StringUtils.hasText(vo.getUserName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名不能为空");
        }
        if (!StringUtils.hasText(vo.getGender())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "性别不能为空");
        }
        if (!StringUtils.hasText(vo.getEmail())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "邮箱不能为空");
        }
        if (!StringUtils.hasText(vo.getPhone())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "手机号不能为空");
        }
        if (!StringUtils.hasText(vo.getPassword()) || vo.getPassword().length() < 6) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "密码不能为空且长度不少于 6 位");
        }
        if (userMapper.selectByUserName(vo.getUserName()) != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名已存在");
        }
        if (userMapper.selectByEmail(vo.getEmail()) != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "邮箱已被注册");
        }
        if (userMapper.selectByPhone(vo.getPhone()) != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "手机号已被注册");
        }

        User user = new User();
        user.setUserName(vo.getUserName());
        user.setLoginPwd(MD5Util.md5(vo.getPassword()));
        user.setGender(vo.getGender());
        user.setEmail(vo.getEmail());
        user.setPhone(vo.getPhone());
        user.setStatus(1);
        user.setAvatar("");
        userMapper.insert(user);

        UserRole userRole = new UserRole();
        userRole.setUid(user.getUid());
        userRole.setRid(DEFAULT_ROLE_ID);
        userRoleMapper.insert(userRole);

        return toVO(user);
    }

    @Override
    public LoginResultVO login(LoginVO vo) {
        if (!StringUtils.hasText(vo.getAccount()) || !StringUtils.hasText(vo.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "账号和密码不能为空");
        }
        User user = userMapper.selectByAccount(vo.getAccount());
        if (user == null || !MD5Util.md5(vo.getPassword()).equals(user.getLoginPwd())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "账号或密码错误");
        }
        return buildResult(user);
    }

    @Override
    public LoginResultVO currentUser(Integer uid) {
        User user = userMapper.selectByUid(uid);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在或已注销");
        }
        return buildResult(user);
    }

    @Override
    public List<User> listByRole(Integer roleId) {
        return userMapper.selectByRole(roleId);
    }

    @Override
    public PageResult<User> userPage(int pageNum, int pageSize, Integer role, String keyword) {
        int offset = (pageNum - 1) * pageSize;
        List<User> list = userMapper.selectPage(offset, pageSize, role, keyword);
        long total = userMapper.countAdmin(role, keyword);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    @Transactional
    public void assignRoles(Integer uid, List<Integer> roleIds) {
        if (userMapper.selectByUid(uid) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (roleIds == null || roleIds.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "至少分配一个角色");
        }
        userRoleMapper.deleteByUid(uid);
        for (Integer rid : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUid(uid);
            userRole.setRid(rid);
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    public void changePassword(Integer uid, String oldPassword, String newPassword) {        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "原密码和新密码不能为空");
        }
        if (newPassword.length() < 6) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "新密码长度不少于 6 位");
        }
        User user = userMapper.selectByUid(uid);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (!MD5Util.md5(oldPassword).equals(user.getLoginPwd())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "原密码不正确");
        }
        User update = new User();
        update.setUid(uid);
        update.setLoginPwd(MD5Util.md5(newPassword));
        userMapper.update(update);
    }

    @Override
    public void updateAvatar(Integer uid, String avatar) {
        User update = new User();
        update.setUid(uid);
        update.setAvatar(avatar);
        userMapper.update(update);
    }

    @Override
    public void updateStatus(Integer uid, Integer status) {
        if (userMapper.selectByUid(uid) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "状态值非法");
        }
        User update = new User();
        update.setUid(uid);
        update.setStatus(status);
        userMapper.update(update);
    }

    @Override
    public void resetPassword(Integer uid, String newPassword) {
        if (userMapper.selectByUid(uid) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "新密码长度不少于 6 位");
        }
        User update = new User();
        update.setUid(uid);
        update.setLoginPwd(MD5Util.md5(newPassword));
        userMapper.update(update);
    }

    private LoginResultVO buildResult(User user) {
        List<Role> roles = userRoleMapper.selectRolesByUid(user.getUid());
        List<String> roleNames = roles.stream().map(Role::getRoleName).collect(Collectors.toList());
        List<Integer> roleIds = roles.stream().map(Role::getRid).collect(Collectors.toList());
        return new LoginResultVO(toVO(user), roleNames, roleIds);
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setUid(user.getUid());
        vo.setUserName(user.getUserName());
        vo.setGender(user.getGender());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
