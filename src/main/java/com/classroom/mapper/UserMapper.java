package com.classroom.mapper;

import com.classroom.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表 Mapper
 */
public interface UserMapper {

    User selectByUid(@Param("uid") Integer uid);

    User selectByUserName(@Param("userName") String userName);

    User selectByEmail(@Param("email") String email);

    User selectByPhone(@Param("phone") String phone);

    /** 登录：账号为邮箱或手机号 */
    User selectByAccount(@Param("account") String account);

    List<User> selectList();

    /** 按角色查询用户（消息接收人选择等） */
    List<User> selectByRole(@Param("roleId") Integer roleId);

    /** 后台用户分页（角色/关键字筛选，联查角色名） */
    List<User> selectPage(@Param("offset") int offset,
                          @Param("limit") int limit,
                          @Param("role") Integer role,
                          @Param("keyword") String keyword);

    long countAdmin(@Param("role") Integer role,
                    @Param("keyword") String keyword);

    int insert(User user);

    int update(User user);

    int deleteByUid(@Param("uid") Integer uid);
}
