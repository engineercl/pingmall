package com.pingmall.search.pojo;

import com.pingmall.common.pojo.PageResult;
import com.pingmall.item.pojo.Brand;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SearchResult extends PageResult<Goods> {
    //三级类目聚合结果集（为了节约带宽只保存类目ID和名称）
    private List<Map<String,Object>> categories;

    //品牌聚合结果集
    private List<Brand> brands;

    //规格参数聚合结果集
    private List<Map<String,Object>> specs;

    //自定义构造函数（包含父类所有属性）
    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Map<String, Object>> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
}
