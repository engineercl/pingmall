package com.pingmall.item.controller;

import com.pingmall.item.pojo.SpecGroup;
import com.pingmall.item.pojo.SpecParam;
import com.pingmall.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 规格参数控制器
 */
@Controller
@RequestMapping("spec")
public class SpecificationController {
    //注入规格参数业务层对象
    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据三级分类查询所有规格参数组
     *
     * @param cid
     * @return
     */
    //请求路径：spec/groups/4
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> getSpecParamGroupsByCategoryId(@PathVariable("cid") Long cid) {

        //查询规格参数组
        List<SpecGroup> specGroups = specificationService.findSpecParamGroupsByCategoryId(cid);
        //判断结果是否为空
        if (CollectionUtils.isEmpty(specGroups))
            //结果为空，返回404
            return ResponseEntity.notFound().build();
        //结果非空
        return ResponseEntity.ok(specGroups);

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
    //请求路径：spec/params?gid=1
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> getSpecParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching
    ) {

        //查询规格参数
        List<SpecParam> specParams = specificationService.findSpecParamsByGroupId(gid, cid, generic, searching);
        //判断结果是否为空
        if (CollectionUtils.isEmpty(specParams))
            //结果为空，返回404
            return ResponseEntity.notFound().build();
        //结果非空
        return ResponseEntity.ok(specParams);

    }

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
