package com.pingmall.auth.controller;

import com.pingmall.auth.config.JwtProperties;
import com.pingmall.auth.pojo.UserInfo;
import com.pingmall.auth.service.AuthService;
import com.pingmall.auth.utils.JwtUtils;
import com.pingmall.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
//启用属性读取类进行配置（括号中指定属性读取类的字节码对象）
@EnableConfigurationProperties({JwtProperties.class})
public class AuthController {
    //要使用属性读取类，首先注入。
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @PostMapping("accredit")
    public ResponseEntity<Void> accredit(@RequestParam("username") String username,
                                         @RequestParam("password") String password,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        //生成用户的JwtToken
        String token = authService.accredit(username, password);
        //判断JwtToken是否成功生成
        if (StringUtils.isBlank(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        //把JwtToken设置到Cookie
        CookieUtils.setCookie(request, response, jwtProperties.getCookieName(), token,
                jwtProperties.getExpire() * 60);
        return ResponseEntity.ok(null);
    }

    /**
     * 校验用户
     *
     * @param token
     * @return
     */
    @GetMapping("verify")
    //@CookieValue注解通过Cookie名称获取值
    public ResponseEntity<UserInfo> verify(@CookieValue("PM_TOKEN") String token,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        UserInfo userInfo = null;
        try {
            //通过JWT工具类使用公钥解析用户Token
            //用户信息保存在Token的载荷部分
            //载荷部分存放的用户信息就是一个UserInfo对象
            userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            //校验未通过
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (userInfo == null)
            //校验未通过
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        //校验通过
        try {
            //刷新JwtToken有效时间
            token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //刷新Cookie中JwtToken的有效时间
        CookieUtils.setCookie(request, response, jwtProperties.getCookieName(), token, jwtProperties.getExpire() * 60);
        //返回用户信息
        return ResponseEntity.ok(userInfo);
    }
}
