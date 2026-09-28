package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.ResultCode;
import com.classroom.entity.ChapterQuiz;
import com.classroom.entity.UserQuizResult;
import com.classroom.mapper.ChapterMapper;
import com.classroom.mapper.ChapterQuizMapper;
import com.classroom.mapper.UserQuizResultMapper;
import com.classroom.service.ChapterQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 章节小测服务实现
 */
@Service
public class ChapterQuizServiceImpl implements ChapterQuizService {

    private static final Set<String> VALID_ANSWERS = new HashSet<>(java.util.Arrays.asList("A", "B", "C", "D"));

    @Autowired
    private ChapterQuizMapper chapterQuizMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private UserQuizResultMapper userQuizResultMapper;

    @Override
    public List<ChapterQuiz> listByChid(Integer chid) {
        return chapterQuizMapper.selectByChid(chid);
    }

    @Override
    public void add(Integer chid, ChapterQuiz quiz) {
        if (chapterMapper.selectByChid(chid) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "章节不存在");
        }
        validate(quiz);
        quiz.setChid(chid);
        if (quiz.getOrders() == null) {
            quiz.setOrders(0);
        }
        chapterQuizMapper.insert(quiz);
    }

    @Override
    public void update(ChapterQuiz quiz) {
        if (quiz.getQid() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "题目 ID 不能为空");
        }
        validate(quiz);
        chapterQuizMapper.update(quiz);
    }

    @Override
    public void delete(Integer qid) {
        chapterQuizMapper.deleteByQid(qid);
    }

    @Override
    public List<ChapterQuiz> quizForStudent(Integer chid, Integer uid) {
        List<ChapterQuiz> list = chapterQuizMapper.selectByChid(chid);
        if (list.isEmpty()) {
            return list;
        }
        List<Integer> qids = list.stream().map(ChapterQuiz::getQid).collect(Collectors.toList());
        List<UserQuizResult> results = userQuizResultMapper.selectByUidQids(uid, qids);
        Map<Integer, UserQuizResult> resultMap = new HashMap<>();
        for (UserQuizResult r : results) {
            resultMap.put(r.getQid(), r);
        }
        for (ChapterQuiz q : list) {
            q.setAnswer(null); // 不向学员暴露答案
            UserQuizResult r = resultMap.get(q.getQid());
            if (r != null) {
                q.setUserAnswer(r.getUserAnswer());
                q.setCorrect(r.getIsCorrect() != null && r.getIsCorrect() == 1);
            }
        }
        return list;
    }

    @Override
    @Transactional
    public Map<String, Object> submit(Integer uid, Integer chid, List<Map<String, Object>> answers) {
        if (answers == null || answers.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请先作答再提交");
        }
        List<ChapterQuiz> quizzes = chapterQuizMapper.selectByChid(chid);
        Map<Integer, ChapterQuiz> quizMap = new HashMap<>();
        for (ChapterQuiz q : quizzes) {
            quizMap.put(q.getQid(), q);
        }
        int correct = 0;
        List<Map<String, Object>> details = new ArrayList<>();
        for (Map<String, Object> a : answers) {
            Integer qid = ((Number) a.get("qid")).intValue();
            String answer = a.get("answer") == null ? "" : String.valueOf(a.get("answer")).trim().toUpperCase();
            ChapterQuiz q = quizMap.get(qid);
            boolean isCorrect = q != null && answer.equals(q.getAnswer());
            if (isCorrect) {
                correct++;
            }
            if (q != null) {
                userQuizResultMapper.upsert(uid, qid, answer.length() > 1 ? answer.substring(0, 1) : answer, isCorrect ? 1 : 0);
            }
            Map<String, Object> d = new HashMap<>();
            d.put("qid", qid);
            d.put("correct", isCorrect);
            if (q != null) {
                d.put("answer", q.getAnswer());
                d.put("question", q.getQuestion());
            }
            details.add(d);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", quizMap.size());
        result.put("correct", correct);
        result.put("details", details);
        return result;
    }

    private void validate(ChapterQuiz quiz) {
        if (!StringUtils.hasText(quiz.getQuestion())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "题目内容不能为空");
        }
        if (!StringUtils.hasText(quiz.getOptionA()) || !StringUtils.hasText(quiz.getOptionB())
                || !StringUtils.hasText(quiz.getOptionC()) || !StringUtils.hasText(quiz.getOptionD())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "四个选项均不能为空");
        }
        if (quiz.getAnswer() == null || !VALID_ANSWERS.contains(quiz.getAnswer().trim().toUpperCase())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "答案必须是 A/B/C/D");
        }
        quiz.setAnswer(quiz.getAnswer().trim().toUpperCase());
    }
}
