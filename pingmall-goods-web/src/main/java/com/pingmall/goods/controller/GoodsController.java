package com.pingmall.goods.controller;

import com.pingmall.goods.service.GoodsHtmlService;
import com.pingmall.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model) {
        Map<String, Object> map = goodsService.loadData(id);
        //把map的所有属性全部设置到model数据模型
        model.addAllAttributes(map);
        //创建静态资源
        goodsHtmlService.creteHtml(id);
        return "item";
    }
}
