package com.pingmall.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pingmall.common.pojo.PageResult;
import com.pingmall.item.mapper.BrandMapper;
import com.pingmall.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 业务层
 */
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据条件分页查询品牌信息
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> findByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化Example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //根据name模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key))
            criteria.andLike("name", "%" + key + "%")
                    .orEqualTo("letter", key);

        //添加分页条件
        PageHelper.startPage(page, rows);

        //添加排序条件
        if (StringUtils.isNotBlank(sortBy))
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));

        //选择相应的通用Mapper方法查询
        List<Brand> brands = brandMapper.selectByExample(example);

        //把查询结果封装到PageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);

        //把查询结果封装到PageBean<Brand>
        return new PageResult<Brand>(pageInfo.getTotal(), pageInfo.getList());

    }

    /**
     * 新增品牌
     *
     * @param brand
     * @param cids
     * @return
     */
    @Transactional
    public void add(Brand brand, List<Long> cids) {

        //新增到品牌表
        brandMapper.insertSelective(brand);

        //新增到中间表
        cids.forEach(cid -> {
            brandMapper.insertCategoryAndBrand(cid, brand.getId());
        });

    }

    /**
     * 查询指定类目下的所有品牌
     *
     * @param cid
     * @return
     */
    public List<Brand> findByCId(Long cid) {

        return brandMapper.selectBrandsByCId(cid);

    }

    /**
     * 根据品牌ID查询品牌对象
     *
     * @param id
     * @return
     */
    public Brand findByBId(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
