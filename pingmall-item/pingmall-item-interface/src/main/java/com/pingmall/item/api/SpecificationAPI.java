package com.pingmall.item.api;

import com.pingmall.item.pojo.SpecGroup;
import com.pingmall.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("spec")
public interface SpecificationAPI {
    /**
     * 根据三级分类查询所有规格参数组
     *
     * @param cid
     * @return
     */
    //请求路径：spec/groups/4
    @GetMapping("groups/{cid}")
    List<SpecGroup> getSpecParamGroupsByCategoryId(@PathVariable("cid") Long cid);

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
    //请求路径：spec/params?gid=1
    @GetMapping("params")
    List<SpecParam> getSpecParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching
    );

    /**
     * 根据类目ID查询规格参数
     *
     * @param cid
     * @return
     */
    //请求路径：params/cid?cid=76
    /*@GetMapping("params/cid")
    public ResponseEntity<List<SpecParam>> getSpecParamsByCId(@RequestParam("cid") Long cid) {
        List<SpecParam> specParams = specificationService.findSpecParamsByCId(cid);
        if (CollectionUtils.isEmpty(specParams))
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(specParams);
    }*/
}
