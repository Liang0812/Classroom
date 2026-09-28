package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.PageResult;
import com.classroom.common.ResultCode;
import com.classroom.entity.Question;
import com.classroom.mapper.QuestionMapper;
import com.classroom.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 问答服务实现
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<Question> listByChapter(Integer chid) {
        return questionMapper.selectByChid(chid);
    }

    @Override
    @Transactional
    public void ask(Integer uid, Integer chid, String question) {
        if (chid == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "缺少章节 ID");
        }
        if (!StringUtils.hasText(question)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "问题内容不能为空");
        }
        if (question.length() > 250) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "问题最多 250 字");
        }
        Question q = new Question();
        q.setUid(uid);
        q.setChid(chid);
        q.setQuestion(question.trim());
        q.setAnswer("");
        q.setStatus(1);
        questionMapper.insert(q);
    }

    @Override
    public List<Question> myQuestions(Integer uid) {
        return questionMapper.selectByUid(uid);
    }

    @Override
    public PageResult<Question> page(int pageNum, int pageSize, Integer status, Integer chid, String keyword) {
        int offset = (pageNum - 1) * pageSize;
        List<Question> list = questionMapper.selectPage(offset, pageSize, status, chid, keyword);
        long total = questionMapper.count(status, chid, keyword);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    @Transactional
    public void reply(Integer qid, String answer) {
        if (qid == null || !StringUtils.hasText(answer)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "回答内容不能为空");
        }
        Question exists = questionMapper.selectByQid(qid);
        if (exists == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "提问不存在");
        }
        questionMapper.reply(qid, answer.trim());
    }
}
