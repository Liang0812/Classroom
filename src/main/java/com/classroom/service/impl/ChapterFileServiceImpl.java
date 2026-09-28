package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.ResultCode;
import com.classroom.entity.ChapterFile;
import com.classroom.mapper.ChapterFileMapper;
import com.classroom.mapper.ChapterMapper;
import com.classroom.service.ChapterFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 章节文件服务实现
 */
@Service
public class ChapterFileServiceImpl implements ChapterFileService {

    @Autowired
    private ChapterFileMapper chapterFileMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Override
    public List<ChapterFile> listByChid(Integer chid) {
        return chapterFileMapper.selectByChid(chid);
    }

    @Override
    public void add(Integer chid, ChapterFile file) {
        if (chapterMapper.selectByChid(chid) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "章节不存在");
        }
        if (!StringUtils.hasText(file.getFileName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件名不能为空");
        }
        if (!StringUtils.hasText(file.getFileUrl())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件地址不能为空");
        }
        file.setChid(chid);
        if (file.getOrders() == null) {
            file.setOrders(0);
        }
        if (file.getFileSize() == null) {
            file.setFileSize(0L);
        }
        chapterFileMapper.insert(file);
    }

    @Override
    public void delete(Integer fileId) {
        chapterFileMapper.deleteByFileId(fileId);
    }
}
