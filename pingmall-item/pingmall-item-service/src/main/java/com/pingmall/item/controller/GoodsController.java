package com.pingmall.item.controller;

import com.pingmall.common.pojo.PageResult;
import com.pingmall.item.pojo.Sku;
import com.pingmall.item.pojo.Spu;
import com.pingmall.item.pojo.SpuDetail;
import com.pingmall.item.pojo.bo.SpuBo;
import com.pingmall.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 根据skuId查询Sku
     */
    @GetMapping("sku/{skuId}")
    public ResponseEntity<Sku> getSkuById(@PathVariable("skuId") Long skuId) {
        Sku sku = goodsService.findSkuById(skuId);
        if (sku == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(sku);
    }

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
    public ResponseEntity<PageResult<SpuBo>> getSpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    ) {

        //分页查询Spu，返回分页对象
        PageResult<SpuBo> result = goodsService.findSpuByPage(key, saleable, page, rows);
        //非空验证
        if (result == null || CollectionUtils.isEmpty(result.getItems()))
            //结果为空，返回404
            return ResponseEntity.notFound().build();
        //结果非空
        return ResponseEntity.ok(result);

    }

    /**
     * 保存商品信息
     *
     * @param spuBo
     * @return
     */
    //请求路径：goods
    //请求参数：
    //{brandId: 1528, title: "00", subTitle: "00",…}
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo) {
        goodsService.add(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据spuId查询spuDetail
     *
     * @param spuId
     * @return
     */
    //请求路径：spu/detail/179
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> getSpuDetail(@PathVariable("spuId") Long spuId) {
        SpuDetail spuDetail = goodsService.findSpuDetail(spuId);
        if (spuDetail == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据spuId查询所有sku
     *
     * @param spuId
     * @return
     */
    //请求路径：sku/list?id=" + oldGoods.id
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> getSkusBySpuId(@RequestParam("id") Long spuId) {

        List<Sku> skus = goodsService.findSkusBySpuId(spuId);
        if (CollectionUtils.isEmpty(skus))
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(skus);

    }

    /**
     * 更新商品信息
     *
     * @param spuBo
     * @return
     */
    //请求路径：goods
    //请求方式：put
    //请求参数：{id: 2, brandId: 8557, cid1: 74, cid2: 75, cid3: 76, title: "华为 G9 青春版 ",…}
    @PutMapping("goods")
    public ResponseEntity<Void> editGoods(@RequestBody SpuBo spuBo) {
        goodsService.edit(spuBo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据ID查询Spu
     *
     * @param spuId
     * @return
     */
    @GetMapping("spu/{spuId}")
    public ResponseEntity<Spu> getSpuById(@PathVariable("spuId") Long spuId) {
        Spu spu = goodsService.findSpuById(spuId);
        if (spu == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(spu);
    }

}
