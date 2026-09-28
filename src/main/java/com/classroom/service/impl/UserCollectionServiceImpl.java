package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.ResultCode;
import com.classroom.entity.UserCollection;
import com.classroom.mapper.UserCollectionMapper;
import com.classroom.service.UserCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 课程收藏服务实现
 */
@Service
public class UserCollectionServiceImpl implements UserCollectionService {

    @Autowired
    private UserCollectionMapper userCollectionMapper;

    @Override
    @Transactional
    public void collect(Integer uid, Integer cuid) {
        if (cuid == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "缺少课程 ID");
        }
        UserCollection uc = userCollectionMapper.selectByUidAndCuid(uid, cuid);
        if (uc == null) {
            uc = new UserCollection();
            uc.setUid(uid);
            uc.setCuid(cuid);
            uc.setStatus(1);
            userCollectionMapper.insert(uc);
        } else {
            uc.setStatus(1);
            userCollectionMapper.update(uc);
        }
    }

    @Override
    @Transactional
    public void cancel(Integer uid, Integer cuid) {
        UserCollection uc = userCollectionMapper.selectByUidAndCuid(uid, cuid);
        if (uc != null) {
            uc.setStatus(0);
            userCollectionMapper.update(uc);
        }
    }

    @Override
    public boolean isCollected(Integer uid, Integer cuid) {
        UserCollection uc = userCollectionMapper.selectByUidAndCuid(uid, cuid);
        return uc != null && uc.getStatus() != null && uc.getStatus() == 1;
    }

    @Override
    public List<UserCollection> mine(Integer uid) {
        return userCollectionMapper.selectMineByUid(uid);
    }
}
