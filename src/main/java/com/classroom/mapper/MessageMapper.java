package com.classroom.mapper;

import com.classroom.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 站内消息表 Mapper
 */
public interface MessageMapper {

    Message selectByMid(@Param("mid") Integer mid);

    /** 我的消息分页：定向(ReceiverUID=uid) + 群发(ReceiverUID=0) */
    List<Message> selectByReceiver(@Param("uid") Integer uid,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

    long countByReceiver(@Param("uid") Integer uid);

    /** 定向未读数 */
    long countUnread(@Param("uid") Integer uid);

    /** 标记已读 */
    int markRead(@Param("mid") Integer mid);

    /** 后台发送记录分页 */
    List<Message> selectPage(@Param("offset") int offset,
                             @Param("limit") int limit,
                             @Param("keyword") String keyword);

    long countAll(@Param("keyword") String keyword);

    int insert(Message message);

    int update(Message message);

    int deleteByMid(@Param("mid") Integer mid);
}
