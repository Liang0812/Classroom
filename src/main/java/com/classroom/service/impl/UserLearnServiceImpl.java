package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.ResultCode;
import com.classroom.entity.Chapter;
import com.classroom.entity.Course;
import com.classroom.entity.UserLearn;
import com.classroom.mapper.ChapterMapper;
import com.classroom.mapper.CourseMapper;
import com.classroom.mapper.UserLearnMapper;
import com.classroom.service.UserLearnService;
import com.classroom.vo.CourseProgressVO;
import com.classroom.vo.PlayInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 学习记录服务实现
 */
@Service
public class UserLearnServiceImpl implements UserLearnService {

    @Autowired
    private UserLearnMapper userLearnMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    @Transactional
    public void start(Integer uid, Integer chid) {
        UserLearn latest = userLearnMapper.selectByUidAndChid(uid, chid);
        if (latest != null && latest.getEndTime() == null) {
            // 已有进行中的记录，不重复记录
            return;
        }
        UserLearn learn = new UserLearn();
        learn.setUid(uid);
        learn.setChid(chid);
        learn.setStatus(1);
        userLearnMapper.insert(learn);
    }

    @Override
    @Transactional
    public void end(Integer uid, Integer chid) {
        UserLearn latest = userLearnMapper.selectByUidAndChid(uid, chid);
        if (latest == null) {
            // 未开始直接结束：兜底记录一条
            UserLearn learn = new UserLearn();
            learn.setUid(uid);
            learn.setChid(chid);
            learn.setStatus(1);
            userLearnMapper.insert(learn);
            latest = userLearnMapper.selectByUidAndChid(uid, chid);
        }
        if (latest.getEndTime() == null) {
            latest.setEndTime(new Date());
            userLearnMapper.update(latest);
        }
    }

    @Override
    @Transactional
    public void savePosition(Integer uid, Integer chid, Double position) {
        if (position == null || position < 0) {
            return;
        }
        UserLearn latest = userLearnMapper.selectByUidAndChid(uid, chid);
        if (latest != null) {
            latest.setRemark(String.valueOf(Math.round(position)));
            userLearnMapper.update(latest);
        }
    }

    @Override
    public List<CourseProgressVO> progress(Integer uid) {
        List<CourseProgressVO> list = userLearnMapper.selectProgressByUid(uid);
        for (CourseProgressVO vo : list) {
            int total = vo.getTotalChapters() == null ? 0 : vo.getTotalChapters();
            int learned = vo.getLearnedChapters() == null ? 0 : vo.getLearnedChapters();
            vo.setProgress(total > 0 ? (int) Math.round(learned * 100.0 / total) : 0);
        }
        return list;
    }

    @Override
    public PlayInfoVO getPlayInfo(Integer uid, Integer chid) {
        Chapter chapter = chapterMapper.selectByChid(chid);
        if (chapter == null || chapter.getStatus() == null || chapter.getStatus() != 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "章节不存在或已下架");
        }
        Course course = courseMapper.selectByCuid(chapter.getCuid());
        if (course == null || course.getStatus() == null || course.getStatus() != 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "课程不存在或已下架");
        }
        PlayInfoVO vo = new PlayInfoVO();
        vo.setChapter(chapter);
        vo.setCourse(course);
        vo.setChapters(chapterMapper.selectByCourseId(chapter.getCuid()));
        // 续播位置：学习记录 remark 中存的是播放秒数
        if (uid != null) {
            UserLearn latest = userLearnMapper.selectByUidAndChid(uid, chid);
            if (latest != null && latest.getRemark() != null) {
                try {
                    vo.setResumePosition(Double.parseDouble(latest.getRemark()));
                } catch (NumberFormatException ignored) {
                    // remark 非数字（历史数据），忽略
                }
            }
        }
        return vo;
    }
}
