package com.pingmall.upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class PingMallCorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        //创建CorsConfiguration对象，它是一个Cors配置类对象
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //添加允许跨域的域名，如果要携带Cookie，不能写“*”，“*”表示所有域名
        corsConfiguration.addAllowedOrigin("http://manage.pingmall.com");
        //设置允许携带Cookie
        corsConfiguration.setAllowCredentials(true);
        //添加允许跨域的请求方式，“*”表示所有请求方式
        corsConfiguration.addAllowedMethod("*");
        //添加允许携带的请求头信息，“*”表示所有头信息
        corsConfiguration.addAllowedHeader("*");

        //创建Cors配置源对象
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        /*使用registerCorsConfiguration()方法初始化Cors配置源对象；
        第一个参数表示对哪些路径进行跨域校验，第二个参数是CorsConfiguration对象。*/
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(corsConfigurationSource);
    }

}
