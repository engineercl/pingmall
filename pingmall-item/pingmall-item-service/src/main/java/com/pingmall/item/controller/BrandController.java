package com.pingmall.item.controller;

import com.pingmall.common.pojo.PageResult;
import com.pingmall.item.pojo.Brand;
import com.pingmall.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 控制器
 */
@Controller
@RequestMapping("brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

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
    /*
    客户端传递的参数
    key=&page=1&rows=5&sortBy=id&desc=false
    */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> getByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) Boolean desc
    ) {
        PageResult<Brand> result = brandService.findByPage(key, page, rows, sortBy, desc);
        //非空验证
        if (CollectionUtils.isEmpty(result.getItems()))
            //结果为空，返回404
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     *
     * @param brand
     * @param cids
     * @return
     */
    //请求参数列表name=hh&image=&cids=3&letter=H
    @PostMapping
    public ResponseEntity<Void> save(
            Brand brand,
            @RequestParam(name = "cids") List<Long> cids) {
        brandService.add(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询指定类目下的所有品牌
     *
     * @param cid
     * @return
     */
    //请求路径：cid/3
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> getByCId(@PathVariable("cid") Long cid) {
        List<Brand> brands = brandService.findByCId(cid);
        if (CollectionUtils.isEmpty(brands))
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(brands);
    }

    /**
     * 根据品牌ID查询品牌对象
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> getByBId(@PathVariable("id") Long id) {
        Brand brand = brandService.findByBId(id);
        if (brand == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(brand);
    }
}
