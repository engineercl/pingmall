package com.pingmall.cart.config;

import com.pingmall.cart.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 * 需要继承WebMvcConfigurer
 */
@Configuration
public class PingMallWebMvcConfiguration implements WebMvcConfigurer {
    //注入要使用的登录拦截器
    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 添加要启用的拦截器
     * registry是拦截器注册对象（Spring已经创建好了）
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /*添加拦截器对象和拦截路径
        “/*”只拦截所有一级路径
        "/**"拦截所有路径*/
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }
}
