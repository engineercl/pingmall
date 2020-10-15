package com.pingmall.item.mapper;

import com.pingmall.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * 持久层
 */
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category, Long> {
}
