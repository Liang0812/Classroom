package com.classroom.entity;

import java.util.Date;

/**
 * 站内消息表 messages（现有库扩展表）
 */
public class Message {

    private Integer mid;
    private Integer senderUid;
    private Integer receiverUid;
    private String title;
    private String content;
    private Integer isRead;
    private Date createTime;

    /** 非表字段：发送者姓名（联查展示用） */
    private String senderName;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public Integer getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(Integer senderUid) {
        this.senderUid = senderUid;
    }

    public Integer getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(Integer receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
