package com.classroom.controller;

import com.classroom.common.BusinessException;
import com.classroom.common.Result;
import com.classroom.common.ResultCode;
import com.classroom.entity.Node;
import com.classroom.entity.Role;
import com.classroom.entity.RoleNode;
import com.classroom.mapper.NodeMapper;
import com.classroom.mapper.RoleMapper;
import com.classroom.mapper.RoleNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 角色权限管理接口（仅管理员）：
 * 角色列表、功能节点列表、角色-节点关系维护
 */
@RestController
@RequestMapping("/api/admin")
public class RoleAdminController {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private RoleNodeMapper roleNodeMapper;

    /** 角色列表 */
    @GetMapping("/roles")
    public Result<List<Role>> roles() {
        return Result.ok(roleMapper.selectAll());
    }

    /** 功能节点列表 */
    @GetMapping("/nodes")
    public Result<List<Node>> nodes() {
        return Result.ok(nodeMapper.selectAll());
    }

    /** 某角色拥有的节点 ID 列表 */
    @GetMapping("/rolenodes/{rid}")
    public Result<List<Integer>> roleNodes(@PathVariable("rid") Integer rid) {
        return Result.ok(roleNodeMapper.selectNidByRid(rid));
    }

    /** 保存角色-节点关系（先清后插） */
    @PutMapping("/rolenodes/{rid}")
    @Transactional
    public Result<Void> saveRoleNodes(@PathVariable("rid") Integer rid, @RequestBody Map<String, Object> body) {
        if (roleMapper.selectByRid(rid) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        roleNodeMapper.deleteByRid(rid);
        Object nidsObj = body.get("nids");
        if (nidsObj instanceof List) {
            for (Object o : (List<?>) nidsObj) {
                int nid = ((Number) o).intValue();
                RoleNode rn = new RoleNode();
                rn.setRid(rid);
                rn.setNid(nid);
                roleNodeMapper.insert(rn);
            }
        }
        return Result.ok();
    }
}
