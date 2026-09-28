package com.classroom.mapper;

import com.classroom.entity.UserLearn;
import com.classroom.vo.CourseProgressVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学习记录表 Mapper
 */
public interface UserLearnMapper {

    UserLearn selectByUcid(@Param("ucid") Integer ucid);

    /** 该用户该章节最新一条学习记录 */
    UserLearn selectByUidAndChid(@Param("uid") Integer uid, @Param("chid") Integer chid);

    List<UserLearn> selectByUid(@Param("uid") Integer uid);

    /** 我的课程学习进度（按课程统计） */
    List<CourseProgressVO> selectProgressByUid(@Param("uid") Integer uid);

    /** 后台学习记录分页（联查学员/课程/章节名） */
    List<UserLearn> selectAdminPage(@Param("offset") int offset,
                                    @Param("limit") int limit);

    long countAdmin();

    int insert(UserLearn userLearn);

    int update(UserLearn userLearn);

    int deleteByUcid(@Param("ucid") Integer ucid);

    /** 按章节 ID 集合物理删除（章节/课程彻底删除前联动清理） */
    int deleteByChids(@Param("chids") List<Integer> chids);
}
