package com.classroom.mapper;

import com.classroom.entity.RoleNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色功能关联表 Mapper
 */
public interface RoleNodeMapper {

    int insert(RoleNode roleNode);

    int deleteByRid(@Param("rid") Integer rid);

    List<Integer> selectNidByRid(@Param("rid") Integer rid);
}
