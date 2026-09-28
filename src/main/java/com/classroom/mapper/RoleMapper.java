package com.classroom.mapper;

import com.classroom.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色表 Mapper
 */
public interface RoleMapper {

    Role selectByRid(@Param("rid") Integer rid);

    List<Role> selectAll();

    int insert(Role role);

    int update(Role role);

    int deleteByRid(@Param("rid") Integer rid);
}
