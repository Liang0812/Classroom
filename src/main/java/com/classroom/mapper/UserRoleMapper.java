package com.classroom.mapper;

import com.classroom.entity.Role;
import com.classroom.entity.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联表 Mapper
 */
public interface UserRoleMapper {

    int insert(UserRole userRole);

    int deleteByUid(@Param("uid") Integer uid);

    List<Integer> selectRidByUid(@Param("uid") Integer uid);

    /** 联查用户拥有的角色 */
    List<Role> selectRolesByUid(@Param("uid") Integer uid);
}
