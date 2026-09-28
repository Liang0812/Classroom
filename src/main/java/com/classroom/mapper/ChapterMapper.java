package com.classroom.mapper;

import com.classroom.entity.Chapter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程章节表 Mapper
 */
public interface ChapterMapper {

    Chapter selectByChid(@Param("chid") Integer chid);

    List<Chapter> selectByCourseId(@Param("cuid") Integer cuid);

    /** 某课程全部章节（含伪删除，课程恢复时联动恢复用） */
    List<Chapter> selectByCourseIdAll(@Param("cuid") Integer cuid);

    int insert(Chapter chapter);

    int update(Chapter chapter);

    /** 伪删除：状态置 0 */
    int deleteByChid(@Param("chid") Integer chid);

    /** 伪删除某课程下全部章节（课程删除时联动） */
    int deleteByCourseId(@Param("cuid") Integer cuid);

    /** 回收站：伪删除章节列表（联查课程名） */
    List<Chapter> selectDeleted();

    /** 恢复：状态置 1 */
    int restore(@Param("chid") Integer chid);

    /** 彻底删除（物理） */
    int deleteByChidPhysical(@Param("chid") Integer chid);

    /** 某课程下全部章节 ID（含伪删除，课程彻底删除前联动清理用） */
    List<Integer> selectChidsByCourse(@Param("cuid") Integer cuid);

    /** 物理删除某课程全部章节（课程彻底删除前联动清理用） */
    int deleteByCourseIdPhysical(@Param("cuid") Integer cuid);
}
