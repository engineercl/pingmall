package com.pingmall.cart.interceptor;

import com.pingmall.auth.pojo.UserInfo;
import com.pingmall.auth.utils.JwtUtils;
import com.pingmall.cart.config.JwtProperties;
import com.pingmall.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 * 获取用户信息
 */
//把当前拦截器添加到Spring容器
@Component
//启用属性读取类JwtProperties
@EnableConfigurationProperties(JwtProperties.class)
//继承一个SpringMVC拦截器的默认实现HandlerInterceptorAdapter
public class LoginInterceptor extends HandlerInterceptorAdapter {
    //注入JwtProperties
    @Autowired
    private JwtProperties jwtProperties;
    //创建ThreadLocal对象来保存本地线程的UserInfo
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 前置方法
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取Cookie中的JwtToken
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        //解析JwtToken获取用户信息
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        //把UserInfo绑定到本地线程
        THREAD_LOCAL.set(userInfo);
        //放行
        return true;
    }

    /**
     * 从ThreadLocal获取本地线程的UserInfo
     * @return
     */
    public static UserInfo getUserInfo(){
        return THREAD_LOCAL.get();
    }

    /**
     * 当前线程所有方法执行完成
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空本地线程绑定的变量（释放内存）
        THREAD_LOCAL.remove();
    }
}
