package com.classroom.entity;

/**
 * 功能（权限）表 nodes
 */
public class Node {

    private Integer nid;
    private String nodeName;
    private String url;
    private Integer parentNode;
    private Integer orders;
    private String nodeExplain;

    public Integer getNid() {
        return nid;
    }

    public void setNid(Integer nid) {
        this.nid = nid;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getParentNode() {
        return parentNode;
    }

    public void setParentNode(Integer parentNode) {
        this.parentNode = parentNode;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public String getNodeExplain() {
        return nodeExplain;
    }

    public void setNodeExplain(String nodeExplain) {
        this.nodeExplain = nodeExplain;
    }
}
