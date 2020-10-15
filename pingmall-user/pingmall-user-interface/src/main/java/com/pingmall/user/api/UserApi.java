package com.pingmall.user.api;

import com.pingmall.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * pingmall-user-service UserService 对外接口
 * 简化了返回值
 */
public interface UserApi {
    /**
     * 用户数据验证
     *
     * @param data
     * @param type
     * @return
     */
    //请求路径：/check/{data}/{type}
    @GetMapping("/check/{data}/{type}")
    Boolean checkUser(@PathVariable("data") String data,
                                             @PathVariable("type") Integer type);

    /**
     * 发送验证码
     *
     * @param phone
     * @return
     */
    @PostMapping("code")
    Void sendVerifyCode(@RequestParam("phone") String phone);

    /**
     * 注册用户
     *
     * @param user
     * @param code
     * @return
     */
    @PostMapping("register")
    Void register(@Valid User user, @RequestParam("code") String code);

    /**
     * 根据用户名和密码查询用户是否存在
     *
     * @param username
     * @param password
     * @return
     */
    //请求方式及路径：GET /query
    //参数：form   String username,String password
    //返回值：JSON格式的User对象
    @PostMapping("query")
    User getUser(@RequestParam("username") String username,
                                      @RequestParam("password") String password);
}
