package com.classroom.mapper;

import com.classroom.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程表 Mapper
 */
public interface CourseMapper {

    Course selectByCuid(@Param("cuid") Integer cuid);

    /** 条件分页查询（类别、关键字、推荐、排序） */
    List<Course> selectList(@Param("cid") Integer cid,
                            @Param("keyword") String keyword,
                            @Param("recommend") Integer recommend,
                            @Param("orderBy") String orderBy,
                            @Param("offset") Integer offset,
                            @Param("limit") Integer limit);

    long count(@Param("cid") Integer cid,
               @Param("keyword") String keyword,
               @Param("recommend") Integer recommend);

    int insert(Course course);

    int update(Course course);

    /** 伪删除：状态置 0 */
    int deleteByCuid(@Param("cuid") Integer cuid);

    /** 回收站：伪删除课程列表 */
    List<Course> selectDeleted();

    /** 恢复：状态置 1 */
    int restore(@Param("cuid") Integer cuid);

    /** 彻底删除（物理） */
    int deleteByCuidPhysical(@Param("cuid") Integer cuid);

    /** 某分类下全部课程 ID（含伪删除，分类彻底删除前联动清理用） */
    List<Integer> selectCidsByCategory(@Param("cid") Integer cid);
}
