package com.classroom.mapper;

import com.classroom.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程类别表 Mapper
 */
public interface CategoryMapper {

    Category selectByCid(@Param("cid") Integer cid);

    /** 全部正常分类（下拉选择用） */
    List<Category> selectList();

    /** 分页查询（可带名称关键字，仅 status=1） */
    List<Category> selectPage(@Param("keyword") String keyword,
                              @Param("offset") Integer offset,
                              @Param("limit") Integer limit);

    long count(@Param("keyword") String keyword);

    int insert(Category category);

    int update(Category category);

    /** 伪删除：状态置 0 */
    int deleteByCid(@Param("cid") Integer cid);

    /** 回收站：伪删除分类列表 */
    List<Category> selectDeleted();

    /** 恢复：状态置 1 */
    int restore(@Param("cid") Integer cid);

    /** 彻底删除 */
    int deleteByCidPhysical(@Param("cid") Integer cid);
}
