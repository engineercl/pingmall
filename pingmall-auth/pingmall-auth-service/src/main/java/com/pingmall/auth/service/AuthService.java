package com.pingmall.auth.service;

import com.pingmall.auth.client.UserClient;
import com.pingmall.auth.config.JwtProperties;
import com.pingmall.auth.pojo.UserInfo;
import com.pingmall.auth.utils.JwtUtils;
import com.pingmall.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    //远程调用pingmall-user-service提供的UserApi接口
    @Autowired
    private UserClient userClient;
    //注入JwtProperties获取密钥
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 生成用户的JwtToken
     * 使用已生成的RSA私钥加密
     *
     * @param username
     * @param password
     * @return
     */
    public String accredit(String username, String password) {
        //查询用户
        User user = userClient.getUser(username, password);
        //校验用户
        if (user == null)
            return null;
        //使用轻量级的UserInfo作为载荷（只包含了id和name）
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        try {
            //生成并返回JwtToken（使用已生成的私钥加密）
            //（参数1：载荷，参数2：已经生成的私钥，参数3：过期时间【分钟】）
            return JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
