package com.classroom.service;

import com.classroom.common.PageResult;
import com.classroom.entity.Message;

/**
 * 站内消息服务
 */
public interface MessageService {

    /** 我的消息分页（定向 + 群发） */
    PageResult<Message> myMessages(Integer uid, int pageNum, int pageSize);

    /** 定向未读数 */
    int unreadCount(Integer uid);

    /** 查看消息详情；若是发给我的未读定向消息则标记已读 */
    Message read(Integer uid, Integer mid);

    /** 发送消息（管理员/教师）：receiverType = all(全体学员) / specific(指定学员) */
    void send(Integer senderUid, String receiverType, Integer receiverUid, String title, String content);

    /** 后台发送记录分页 */
    PageResult<Message> adminPage(int pageNum, int pageSize, String keyword);
}
