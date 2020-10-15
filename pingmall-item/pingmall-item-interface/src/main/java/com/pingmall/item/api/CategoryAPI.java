package com.pingmall.item.api;

import com.pingmall.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping("category")
public interface CategoryAPI {
    /**
     * 根据父类目ID查询所有子类目
     *
     * @param pid
     * @return
     */
    @GetMapping("list")
    @ResponseBody
    List<Category> getCategoriesByPID(@RequestParam(value = "pid", defaultValue = "0") Long pid);

    /**
     * 根据类目ID查询类目名称集合
     *
     * @param ids
     * @return
     */
    @GetMapping
    List<String> getNamesByIds(@RequestParam("ids") List<Long> ids);
}
