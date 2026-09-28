package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.ResultCode;
import com.classroom.entity.Category;
import com.classroom.entity.Chapter;
import com.classroom.entity.Course;
import com.classroom.entity.Question;
import com.classroom.mapper.CategoryMapper;
import com.classroom.mapper.ChapterMapper;
import com.classroom.mapper.CourseMapper;
import com.classroom.mapper.QuestionMapper;
import com.classroom.mapper.UserCollectionMapper;
import com.classroom.mapper.UserLearnMapper;
import com.classroom.service.RecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 回收站服务实现
 */
@Service
public class RecycleServiceImpl implements RecycleService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserCollectionMapper userCollectionMapper;

    @Autowired
    private UserLearnMapper userLearnMapper;

    /* ---------- 分类 ---------- */

    @Override
    public List<Category> deletedCategories() {
        return categoryMapper.selectDeleted();
    }

    @Override
    @Transactional
    public void restoreCategory(Integer cid) {
        requireExists(categoryMapper.selectByCid(cid), "分类不存在");
        categoryMapper.restore(cid);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer cid) {
        requireExists(categoryMapper.selectByCid(cid), "分类不存在");
        // 该分类下课程（含伪删除）联动彻底删除，避免外键冲突
        List<Integer> cids = courseMapper.selectCidsByCategory(cid);
        for (Integer cuid : cids) {
            deleteCourse(cuid);
        }
        categoryMapper.deleteByCidPhysical(cid);
    }

    /* ---------- 课程 ---------- */

    @Override
    public List<Course> deletedCourses() {
        return courseMapper.selectDeleted();
    }

    @Override
    @Transactional
    public void restoreCourse(Integer cuid) {
        requireExists(courseMapper.selectByCuid(cuid), "课程不存在");
        courseMapper.restore(cuid);
        // 课程伪删除时章节联动伪删除，恢复时一并恢复
        List<Chapter> chapters = chapterMapper.selectByCourseIdAll(cuid);
        for (Chapter ch : chapters) {
            chapterMapper.restore(ch.getChid());
        }
    }

    @Override
    @Transactional
    public void deleteCourse(Integer cuid) {
        requireExists(courseMapper.selectByCuid(cuid), "课程不存在");
        List<Integer> chids = chapterMapper.selectChidsByCourse(cuid);
        if (!chids.isEmpty()) {
            userLearnMapper.deleteByChids(chids);
            questionMapper.deleteByChids(chids);
        }
        userCollectionMapper.deleteByCuid(cuid);
        chapterMapper.deleteByCourseIdPhysical(cuid);
        courseMapper.deleteByCuidPhysical(cuid);
    }

    /* ---------- 章节 ---------- */

    @Override
    public List<Chapter> deletedChapters() {
        return chapterMapper.selectDeleted();
    }

    @Override
    @Transactional
    public void restoreChapter(Integer chid) {
        requireExists(chapterMapper.selectByChid(chid), "章节不存在");
        chapterMapper.restore(chid);
    }

    @Override
    @Transactional
    public void deleteChapter(Integer chid) {
        requireExists(chapterMapper.selectByChid(chid), "章节不存在");
        userLearnMapper.deleteByChids(Collections.singletonList(chid));
        questionMapper.deleteByChids(Collections.singletonList(chid));
        chapterMapper.deleteByChidPhysical(chid);
    }

    /* ---------- 提问 ---------- */

    @Override
    public List<Question> deletedQuestions() {
        return questionMapper.selectDeleted();
    }

    @Override
    @Transactional
    public void restoreQuestion(Integer qid) {
        requireExists(questionMapper.selectByQid(qid), "提问不存在");
        questionMapper.restore(qid);
    }

    @Override
    @Transactional
    public void deleteQuestion(Integer qid) {
        requireExists(questionMapper.selectByQid(qid), "提问不存在");
        questionMapper.deleteByQidPhysical(qid);
    }

    private void requireExists(Object obj, String msg) {
        if (obj == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, msg);
        }
    }
}
