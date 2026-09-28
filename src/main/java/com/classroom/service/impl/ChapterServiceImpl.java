package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.ResultCode;
import com.classroom.entity.Chapter;
import com.classroom.mapper.ChapterMapper;
import com.classroom.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 课程章节服务实现
 */
@Service
public class ChapterServiceImpl implements ChapterService {

    @Autowired
    private ChapterMapper chapterMapper;

    @Override
    public List<Chapter> listByCourse(Integer cuid) {
        return chapterMapper.selectByCourseId(cuid);
    }

    @Override
    @Transactional
    public void add(Chapter chapter) {
        if (chapter == null || chapter.getCuid() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "缺少所属课程");
        }
        if (!StringUtils.hasText(chapter.getChapterName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "章节名称不能为空");
        }
        chapter.setOrders(chapter.getOrders() == null ? 0 : chapter.getOrders());
        chapter.setStatus(1);
        chapterMapper.insert(chapter);
    }

    @Override
    public void update(Chapter chapter) {
        if (chapter == null || chapter.getChid() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "缺少章节 ID");
        }
        if (!StringUtils.hasText(chapter.getChapterName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "章节名称不能为空");
        }
        chapterMapper.update(chapter);
    }

    @Override
    public void remove(Integer chid) {
        chapterMapper.deleteByChid(chid);
    }
}
