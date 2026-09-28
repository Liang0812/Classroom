package com.classroom.controller;

import com.classroom.common.BusinessException;
import com.classroom.common.PageResult;
import com.classroom.common.Result;
import com.classroom.common.ResultCode;
import com.classroom.entity.Message;
import com.classroom.service.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学员端消息接口（需登录）
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /** 我的消息分页 */
    @GetMapping
    public Result<PageResult<Message>> list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                            HttpSession session) {
        return Result.ok(messageService.myMessages(requireUid(session), pageNum, pageSize));
    }

    /** 定向未读数 */
    @GetMapping("/unread-count")
    public Result<Integer> unreadCount(HttpSession session) {
        return Result.ok(messageService.unreadCount(requireUid(session)));
    }

    /** 消息详情（定向未读自动标记已读） */
    @GetMapping("/{mid}")
    public Result<Message> detail(@PathVariable("mid") Integer mid, HttpSession session) {
        return Result.ok(messageService.read(requireUid(session), mid));
    }

    private Integer requireUid(HttpSession session) {
        Object uid = session.getAttribute("loginUserId");
        if (uid == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        return (Integer) uid;
    }
}
