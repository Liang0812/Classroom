package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.PageResult;
import com.classroom.common.ResultCode;
import com.classroom.entity.Message;
import com.classroom.mapper.MessageMapper;
import com.classroom.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 站内消息服务实现
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public PageResult<Message> myMessages(Integer uid, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Message> list = messageMapper.selectByReceiver(uid, offset, pageSize);
        long total = messageMapper.countByReceiver(uid);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public int unreadCount(Integer uid) {
        return (int) messageMapper.countUnread(uid);
    }

    @Override
    @Transactional
    public Message read(Integer uid, Integer mid) {
        Message msg = messageMapper.selectByMid(mid);
        if (msg == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "消息不存在");
        }
        // 仅对发给当前用户的未读定向消息标记已读（群发消息不标记，避免互相影响）
        if (msg.getReceiverUid() != null && msg.getReceiverUid() == uid
                && msg.getIsRead() != null && msg.getIsRead() == 0) {
            messageMapper.markRead(mid);
            msg.setIsRead(1);
        }
        return msg;
    }

    @Override
    @Transactional
    public void send(Integer senderUid, String receiverType, Integer receiverUid, String title, String content) {
        if (!StringUtils.hasText(title)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "消息标题不能为空");
        }
        if (!StringUtils.hasText(content)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "消息内容不能为空");
        }
        if (title.length() > 100) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "标题最多 100 字");
        }
        if (content.length() > 500) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "内容最多 500 字");
        }
        Message msg = new Message();
        msg.setSenderUid(senderUid);
        if ("specific".equals(receiverType)) {
            if (receiverUid == null) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "请选择接收学员");
            }
            msg.setReceiverUid(receiverUid);
        } else {
            // 默认全体学员
            msg.setReceiverUid(0);
        }
        msg.setTitle(title.trim());
        msg.setContent(content.trim());
        msg.setIsRead(0);
        messageMapper.insert(msg);
    }

    @Override
    public PageResult<Message> adminPage(int pageNum, int pageSize, String keyword) {
        int offset = (pageNum - 1) * pageSize;
        List<Message> list = messageMapper.selectPage(offset, pageSize, keyword);
        long total = messageMapper.countAll(keyword);
        return new PageResult<>(list, total, pageNum, pageSize);
    }
}
