package com.classroom.service.impl;

import com.classroom.common.BusinessException;
import com.classroom.common.PageResult;
import com.classroom.common.ResultCode;
import com.classroom.entity.Category;
import com.classroom.mapper.CategoryMapper;
import com.classroom.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 课程分类服务实现
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageResult<Category> page(int pageNum, int pageSize, String keyword) {
        int offset = (pageNum - 1) * pageSize;
        List<Category> list = categoryMapper.selectPage(keyword, offset, pageSize);
        long total = categoryMapper.count(keyword);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public List<Category> listAll() {
        return categoryMapper.selectList();
    }

    @Override
    @Transactional
    public void add(Category category) {
        if (category == null || !StringUtils.hasText(category.getCategoryName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "分类名称不能为空");
        }
        category.setParentId(category.getParentId() == null ? 0 : category.getParentId());
        category.setOrders(category.getOrders() == null ? 0 : category.getOrders());
        category.setStatus(1);
        categoryMapper.insert(category);
    }

    @Override
    public void update(Category category) {
        if (category == null || category.getCid() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "缺少分类 ID");
        }
        if (!StringUtils.hasText(category.getCategoryName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "分类名称不能为空");
        }
        categoryMapper.update(category);
    }

    @Override
    public void remove(Integer cid) {
        categoryMapper.deleteByCid(cid);
    }
}
