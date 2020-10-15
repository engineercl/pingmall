package com.pingmall.item.pojo.bo;

import com.pingmall.item.pojo.Sku;
import com.pingmall.item.pojo.Spu;
import com.pingmall.item.pojo.SpuDetail;
import lombok.Data;

import java.util.List;

/**
 * Spu扩展属性类
 */
@Data
public class SpuBo extends Spu {

    private String cname;           // 商品分类名称
    private String bname;           // 品牌名称
    private SpuDetail spuDetail;    // 商品详情
    private List<Sku> skus;         // sku列表

}
