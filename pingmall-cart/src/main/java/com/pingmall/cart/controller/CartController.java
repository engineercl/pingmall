package com.pingmall.cart.controller;

import com.pingmall.cart.pojo.Cart;
import com.pingmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 保存登录用户的购物车数据
     * @param cart
     * @return
     */
    @PostMapping
    //使用@RequestBody接收JSON对象类型参数
    public ResponseEntity<Void> saveCart(@RequestBody Cart cart) {
        //保存购物车数据到Redis
        cartService.saveCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询登录用户的购物车数据
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Cart>> getCarts(){
        List<Cart> carts = cartService.findCarts();
        if (CollectionUtils.isEmpty(carts))
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(carts);
    }

    /**
     * 保存登录用户的购物车商品数量
     * @param cart
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> saveNum(@RequestBody Cart cart){
        cartService.saveNum(cart);
        return ResponseEntity.noContent().build();
    }
}
