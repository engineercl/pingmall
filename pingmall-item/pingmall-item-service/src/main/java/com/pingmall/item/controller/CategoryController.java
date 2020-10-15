package com.pingmall.item.controller;

import com.pingmall.item.pojo.Category;
import com.pingmall.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 控制器
 */
@Controller
@RequestMapping("category")
public class CategoryController {
    //业务层对象
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父类目ID查询所有子类目
     *
     * @param pid
     * @return
     */
    @GetMapping("list")
    @ResponseBody
    public ResponseEntity<List<Category>> getCategoriesByPID(@RequestParam(value = "pid", defaultValue = "0") Long pid) {
        //验证参数
        if (pid < 0)
            //非法请求
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().build();

        //查询类目
        List<Category> categories = categoryService.findCategoriesByPID(pid);

        //非空验证
        if (categories.isEmpty())
            //404，资源未找到
            return ResponseEntity.notFound().build();

        //200，查询已成功
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据类目ID查询类目名称集合
     * @param ids
     * @return
     */
    @GetMapping
    public ResponseEntity<List<String>> getNamesByIds(@RequestParam("ids")List<Long> ids){
        List<String> categoryNames = categoryService.findByCategoryIds(ids);
        if (CollectionUtils.isEmpty(categoryNames))
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(categoryNames);
    }
}
