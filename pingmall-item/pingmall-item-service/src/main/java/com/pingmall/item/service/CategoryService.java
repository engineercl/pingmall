package com.pingmall.item.service;

import com.pingmall.item.mapper.CategoryMapper;
import com.pingmall.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务层
 */
@Service
public class CategoryService {

    //持久层对象
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父类目ID查询所有子类目
     *
     * @param pid
     * @return
     */
    public List<Category> findCategoriesByPID(Long pid) {

        Category category = new Category();
        category.setParentId(pid);
        return categoryMapper.select(category);

    }

    /**
     * 根据多个类目ID查询类目名称
     *
     * @param ids
     * @return
     */
    public List<String> findByCategoryIds(List<Long> ids) {

        List<Category> categories = categoryMapper.selectByIdList(ids);
        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());

    }

}
