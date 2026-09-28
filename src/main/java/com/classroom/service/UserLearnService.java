package com.classroom.service;

import com.classroom.vo.CourseProgressVO;
import com.classroom.vo.PlayInfoVO;

import java.util.List;

/**
 * 学习记录服务：视频点播的开始/结束记录、播放信息、学习进度
 */
public interface UserLearnService {

    /** 开始学习某章节（有进行中记录则不重复） */
    void start(Integer uid, Integer chid);

    /** 结束学习某章节（记录结束时间） */
    void end(Integer uid, Integer chid);

    /** 保存播放位置（秒），写入学习记录 remark 字段用于续播 */
    void savePosition(Integer uid, Integer chid, Double position);

    /** 我的课程学习进度 */
    List<CourseProgressVO> progress(Integer uid);

    /** 播放页信息：章节 + 课程 + 同课程章节列表（uid 非空时附带续播位置） */
    PlayInfoVO getPlayInfo(Integer uid, Integer chid);
}
