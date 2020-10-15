package com.pingmall.goods.service;

import com.pingmall.goods.client.BrandClient;
import com.pingmall.goods.client.CategoryClient;
import com.pingmall.goods.client.GoodsClient;
import com.pingmall.goods.client.SpecificationClient;
import com.pingmall.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsService {
    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    /**
     * 根据spuId查询商品详情相关数据
     *
     * @param spuId
     * @return
     */
    public Map<String, Object> loadData(Long spuId) {
        Map<String, Object> model = new HashMap<>();
        //查询Spu
        Spu spu = goodsClient.getSpuById(spuId);
        //查询SpuDetail
        SpuDetail spuDetail = goodsClient.getSpuDetail(spuId);
        //查询Brand
        Brand brand = brandClient.getByBId(spu.getBrandId());
        //查询商品对应的所有级别类目（每个类目对应一个Map集合）
        List<Long> categoryIds = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> categoryNames = categoryClient.getNamesByIds(categoryIds);
        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < categoryIds.size(); i++) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("id", categoryIds.get(i));
            categoryMap.put("name", categoryNames.get(i));
            categories.add(categoryMap);
        }
        //查询对应的所有Sku
        List<Sku> skus = goodsClient.getSkusBySpuId(spuId);
        //根据3级类目查询规格参数组
        List<SpecGroup> groups = specificationClient.getSpecParamGroupsByCategoryId(spu.getCid3());
        //查询规格参数组下的所有参数
        groups.forEach(specGroup -> {
            List<SpecParam> specParams = specificationClient.getSpecParams
                    (specGroup.getId(), null, null, null);
            specGroup.setParams(specParams);
        });
        //查询特殊规格参数（每个参数对应一个Map元素）
        List<SpecParam> specParams = specificationClient.getSpecParams(
                null, spu.getCid3(), false, null);
        Map<Long, String> paramMap = new HashMap<>();
        specParams.forEach(specParam -> {
            //specParam.id作为Map的Key
            //specParam.name作为Map的值
            paramMap.put(specParam.getId(), specParam.getName());
        });
        //页面加载需要以下数据
        model.put("spu", spu);
        model.put("spuDetail", spuDetail);
        model.put("categories", categories);
        model.put("brand", brand);
        model.put("skus", skus);
        model.put("groups", groups);
        model.put("paramMap", paramMap);
        return model;
    }
}
