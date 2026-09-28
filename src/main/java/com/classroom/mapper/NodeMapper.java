package com.classroom.mapper;

import com.classroom.entity.Node;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能（权限）表 Mapper
 */
public interface NodeMapper {

    Node selectByNid(@Param("nid") Integer nid);

    List<Node> selectAll();

    /** 查询角色拥有的功能节点 */
    List<Node> selectByRoleId(@Param("rid") Integer rid);

    int insert(Node node);

    int update(Node node);

    int deleteByNid(@Param("nid") Integer nid);
}
