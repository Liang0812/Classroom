package com.classroom.service;

import com.classroom.entity.UserCollection;

import java.util.List;

/**
 * 课程收藏服务
 */
public interface UserCollectionService {

    /** 收藏课程（幂等：已收藏则保持收藏） */
    void collect(Integer uid, Integer cuid);

    /** 取消收藏 */
    void cancel(Integer uid, Integer cuid);

    /** 是否已收藏 */
    boolean isCollected(Integer uid, Integer cuid);

    /** 我的收藏列表（联查课程信息） */
    List<UserCollection> mine(Integer uid);
}
