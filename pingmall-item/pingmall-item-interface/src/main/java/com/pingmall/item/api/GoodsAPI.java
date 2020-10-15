package com.pingmall.item.api;

import com.pingmall.common.pojo.PageResult;
import com.pingmall.item.pojo.Sku;
import com.pingmall.item.pojo.Spu;
import com.pingmall.item.pojo.SpuDetail;
import com.pingmall.item.pojo.bo.SpuBo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsAPI {
    /**
     * 根据skuId查询Sku
     */
    @GetMapping("sku/{skuId}")
    Sku getSkuById(@PathVariable("skuId") Long skuId);

    /**
     * 根据spuId查询spuDetail
     *
     * @param spuId
     * @return
     */
    //请求路径：spu/detail/179
    @GetMapping("spu/detail/{spuId}")
    SpuDetail getSpuDetail(@PathVariable("spuId") Long spuId);

    /**
     * 分页查询商品信息
     *
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    //请求路径：spu/page?key=&saleable=true&page=1&rows=5
    @GetMapping("spu/page")
    PageResult<SpuBo> getSpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    );

    /**
     * 根据spuId查询所有sku
     *
     * @param spuId
     * @return
     */
    //请求路径：sku/list?id=" + oldGoods.id
    @GetMapping("sku/list")
    List<Sku> getSkusBySpuId(@RequestParam("id") Long spuId);

    /**
     * 根据ID查询Spu
     *
     * @param spuId
     * @return
     */
    @GetMapping("spu/{spuId}")
    Spu getSpuById(@PathVariable("spuId") Long spuId);

}
