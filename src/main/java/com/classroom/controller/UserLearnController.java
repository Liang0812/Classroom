package com.classroom.controller;

import com.classroom.common.BusinessException;
import com.classroom.common.Result;
import com.classroom.common.ResultCode;
import com.classroom.service.UserLearnService;
import com.classroom.vo.CourseProgressVO;
import com.classroom.vo.PlayInfoVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 学习记录接口（需登录）
 */
@RestController
@RequestMapping("/api/learn")
public class UserLearnController {

    @Autowired
    private UserLearnService userLearnService;

    /** 播放页信息：章节 + 课程 + 章节列表（含续播位置） */
    @GetMapping("/chapter/{chid}")
    public Result<PlayInfoVO> playInfo(@PathVariable("chid") Integer chid, HttpSession session) {
        return Result.ok(userLearnService.getPlayInfo(requireUid(session), chid));
    }

    /** 保存播放位置（秒），用于下次续播 */
    @PostMapping("/position")
    public Result<Void> savePosition(@RequestBody Map<String, Object> body, HttpSession session) {
        Object chidObj = body.get("chid");
        Object posObj = body.get("position");
        if (chidObj == null || posObj == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "参数不完整");
        }
        userLearnService.savePosition(requireUid(session), ((Number) chidObj).intValue(), ((Number) posObj).doubleValue());
        return Result.ok();
    }

    /** 开始学习（记录开始时间） */
    @PostMapping("/start")
    public Result<Void> start(@RequestParam(value = "chid") Integer chid, HttpSession session) {
        userLearnService.start(requireUid(session), chid);
        return Result.ok();
    }

    /** 结束学习（记录结束时间，页面卸载时前端用 sendBeacon 调用） */
    @PostMapping("/end")
    public Result<Void> end(@RequestParam(value = "chid") Integer chid, HttpSession session) {
        userLearnService.end(requireUid(session), chid);
        return Result.ok();
    }

    /** 我的课程学习进度 */
    @GetMapping("/progress")
    public Result<List<CourseProgressVO>> progress(HttpSession session) {
        return Result.ok(userLearnService.progress(requireUid(session)));
    }

    private Integer requireUid(HttpSession session) {
        Object uid = session.getAttribute("loginUserId");
        if (uid == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        return (Integer) uid;
    }
}
