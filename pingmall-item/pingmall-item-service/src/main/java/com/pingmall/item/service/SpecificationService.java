package com.pingmall.item.service;

import com.pingmall.item.mapper.SpecGroupMapper;
import com.pingmall.item.mapper.SpecParamMapper;
import com.pingmall.item.pojo.SpecGroup;
import com.pingmall.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 规格参数业务层
 */
@Service
public class SpecificationService {

    //注入规格参数组Mapper对象
    @Autowired
    private SpecGroupMapper specGroupMapper;
    //注入规格参数Mapper对象
    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据三级分类查询所有规格参数组
     *
     * @param cid
     * @return
     */
    public List<SpecGroup> findSpecParamGroupsByCategoryId(Long cid) {

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return specGroupMapper.select(specGroup);

    }

    /**
     * 根据组ID,
     * 或类目ID,
     * 或是否是通用，
     * 或是否是搜索过滤，
     * 查询所有规格参数
     *
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> findSpecParamsByGroupId(Long gid, Long cid, Boolean generic, Boolean searching) {

        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return specParamMapper.select(specParam);

    }

    /**
     * 根据类目ID查询规格参数
     *
     * @param cid
     * @return
     */
    /*public List<SpecParam> findSpecParamsByCId(Long cid) {

        //获取类目下的规格参数组
        List<SpecGroup> specGroups = findSpecParamGroupsByCategoryId(cid);
        //获取规格参数组全部ID
        List<Long> specGroupIds = specGroups.stream().map(specGroup ->
                specGroup.getId()
        ).collect(Collectors.toList());
        //查询规格参数组下的所有规格参数
        //创建example对象
        Example example = new Example(SpecParam.class);
        //创建条件对象
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("groupId", specGroupIds);
        return specParamMapper.selectByExample(example);

    }*/

}
