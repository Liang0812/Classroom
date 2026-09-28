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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 后台消息管理接口（管理员/教师发送通知）
 */
@RestController
@RequestMapping("/api/admin/messages")
public class AdminMessageController {

    @Autowired
    private MessageService messageService;

    /** 发送记录分页 */
    @GetMapping
    public Result<PageResult<Message>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                            @RequestParam(value = "keyword", required = false) String keyword) {
        return Result.ok(messageService.adminPage(pageNum, pageSize, keyword));
    }

    /**
     * 发送消息
     * body: { receiverType: "all" | "specific", receiverUid?: int, title, content }
     */
    @PostMapping
    public Result<Void> send(@RequestBody Map<String, Object> body, HttpSession session) {
        Integer senderUid = requireUid(session);
        String receiverType = body.get("receiverType") == null ? "all" : body.get("receiverType").toString();
        Object ru = body.get("receiverUid");
        Integer receiverUid = ru == null ? null : ((Number) ru).intValue();
        String title = body.get("title") == null ? null : body.get("title").toString();
        String content = body.get("content") == null ? null : body.get("content").toString();
        messageService.send(senderUid, receiverType, receiverUid, title, content);
        return Result.ok();
    }

    private Integer requireUid(HttpSession session) {
        Object uid = session.getAttribute("loginUserId");
        if (uid == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        return (Integer) uid;
    }
}
