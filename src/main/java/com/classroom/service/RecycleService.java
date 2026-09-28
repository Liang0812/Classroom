package com.classroom.service;

import com.classroom.entity.Category;
import com.classroom.entity.Chapter;
import com.classroom.entity.Course;
import com.classroom.entity.Question;

import java.util.List;

/**
 * 回收站服务：伪删除数据的恢复与彻底删除（含关联清理）
 */
public interface RecycleService {

    /* 分类 */
    List<Category> deletedCategories();

    void restoreCategory(Integer cid);

    void deleteCategory(Integer cid);

    /* 课程 */
    List<Course> deletedCourses();

    void restoreCourse(Integer cuid);

    void deleteCourse(Integer cuid);

    /* 章节 */
    List<Chapter> deletedChapters();

    void restoreChapter(Integer chid);

    void deleteChapter(Integer chid);

    /* 提问 */
    List<Question> deletedQuestions();

    void restoreQuestion(Integer qid);

    void deleteQuestion(Integer qid);
}
