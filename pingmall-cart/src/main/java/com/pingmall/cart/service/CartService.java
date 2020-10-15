package com.pingmall.cart.service;

import com.pingmall.auth.pojo.UserInfo;
import com.pingmall.cart.client.GoodsClient;
import com.pingmall.cart.interceptor.LoginInterceptor;
import com.pingmall.cart.pojo.Cart;
import com.pingmall.common.utils.JsonUtils;
import com.pingmall.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    //注入Redis模板对象
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //指定Redis中Key的前缀
    private static final String KEY_PREFIX = "user:cart:";

    @Autowired
    private GoodsClient goodsClient;

    /**
     * 保存登录用户的购物车数据到Redis中
     *
     * @param cart
     */
    public void saveCart(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        //根据用户查询现有的购物车数据
        BoundHashOperations<String, Object, Object> hashOptions = stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        //用户选择的SkuId
        String key = cart.getSkuId().toString();
        //用户选择的购买数量
        Integer num = cart.getNum();

        //判断是否已存在当前Sku
        if (hashOptions.hasKey(key)) {
            //如果存在则添加数量
            String cartJson = hashOptions.get(key).toString();
            cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum() + num);
        } else {
            //如果不存在添加当前Sku
            //查询Sku的其它部分数据
            Sku sku = goodsClient.getSkuById(cart.getSkuId());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ?
                    "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setOwnSpecs(sku.getOwnSpec());
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setUserId(userInfo.getId());
        }

        //更新Redis中的购物车数据
        hashOptions.put(key, JsonUtils.serialize(cart));
    }

    /**
     * 查询登录用户的购物车数据
     *
     * @return
     */
    public List<Cart> findCarts() {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        //判断用户有无购物车记录
        if (!stringRedisTemplate.hasKey(KEY_PREFIX+userInfo.getId()))
            return null;

        //根据用户查询现有的购物车数据
        BoundHashOperations<String, Object, Object> hashOptions = stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        //获取所有购物车项（每一个value是一个Cart对象JSON字符串）
        List<Object> cartsJson = hashOptions.values();

        //遍历cartsJson依次把每个Cart对象JSON字符串转换为Cart对象并添加到一个新的List集合返回
        return cartsJson.stream().map(cartJson -> JsonUtils.parse(cartJson.toString(), Cart.class)).collect(Collectors.toList());
    }

    /**
     * 保存登录用户的购物车商品数量
     * @param cart
     * @return
     */
    public void saveNum(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        //根据用户查询现有的购物车数据
        BoundHashOperations<String, Object, Object> hashOptions = stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        //用户选择的购买数量
        Integer num = cart.getNum();

        //从Redis获取当前购物车项（是一个Cart类型JSON字符串）
        String cartJson = hashOptions.get(cart.getSkuId().toString()).toString();
        //转换为Cart对象
        cart = JsonUtils.parse(cartJson, Cart.class);

        //修改购买数量
        cart.setNum(num);

        //重新保存到Redis
        hashOptions.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
    }
}
