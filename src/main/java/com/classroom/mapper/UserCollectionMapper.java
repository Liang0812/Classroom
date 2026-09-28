package com.classroom.mapper;

import com.classroom.entity.UserCollection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户收藏表 Mapper
 */
public interface UserCollectionMapper {

    UserCollection selectByCoid(@Param("coid") Integer coid);

    UserCollection selectByUidAndCuid(@Param("uid") Integer uid, @Param("cuid") Integer cuid);

    List<UserCollection> selectByUid(@Param("uid") Integer uid);

    /** 我的收藏列表（联查课程信息） */
    List<UserCollection> selectMineByUid(@Param("uid") Integer uid);

    int insert(UserCollection userCollection);

    int update(UserCollection userCollection);

    int deleteByCoid(@Param("coid") Integer coid);

    /** 按课程物理删除收藏（课程彻底删除前联动清理） */
    int deleteByCuid(@Param("cuid") Integer cuid);
}
