package com.pingmall.item.api;

import com.pingmall.common.pojo.PageResult;
import com.pingmall.item.pojo.Brand;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("brand")
public interface BrandAPI {
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
    PageResult<Brand> getByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) Boolean desc
    );

    /**
     * 新增品牌
     *
     * @param brand
     * @param cids
     * @return
     */
    //请求参数列表name=hh&image=&cids=3&letter=H
    @PostMapping
    void save(
            Brand brand,
            @RequestParam(name = "cids") List<Long> cids);

    /**
     * 查询指定类目下的所有品牌
     *
     * @param cid
     * @return
     */
    //请求路径：cid/3
    @GetMapping("cid/{cid}")
    List<Brand> getByCId(@PathVariable("cid") Long cid);

    /**
     * 根据品牌ID查询品牌对象
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    Brand getByBId(@PathVariable("id") Long id);
}
