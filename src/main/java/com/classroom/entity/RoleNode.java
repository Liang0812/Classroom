package com.classroom.entity;

import java.util.Date;

/**
 * 角色功能关联表 rolenodes
 */
public class RoleNode {

    private Integer rid;
    private Integer nid;
    private Date createTime;

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public Integer getNid() {
        return nid;
    }

    public void setNid(Integer nid) {
        this.nid = nid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
