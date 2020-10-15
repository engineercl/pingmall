package com.pingmall.user.controller;

import com.pingmall.user.pojo.User;
import com.pingmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户数据验证
     *
     * @param data
     * @param type
     * @return
     */
    //请求路径：/check/{data}/{type}
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data") String data,
                                             @PathVariable("type") Integer type) {
        Boolean flag = userService.checkUser(data, type);
        if (flag == null)
            //返回非法请求
            return ResponseEntity.badRequest().build();
        //返回验证结果
        return ResponseEntity.ok(flag);
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @return
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {
        userService.sendVerifyCode(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 注册用户
     *
     * @param user
     * @param code
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code) {
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

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
    public ResponseEntity<User> getUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        //查询用户是否存在
        User record = userService.findUser(username, password);
        if (record != null)
            return ResponseEntity.ok(record);
        return ResponseEntity.badRequest().build();
    }
}
