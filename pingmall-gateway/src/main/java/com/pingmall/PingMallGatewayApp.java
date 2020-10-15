package com.pingmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
//启用服务发现
@EnableDiscoveryClient
public class PingMallGatewayApp {

    public static void main(String[] args) {
        SpringApplication.run(PingMallGatewayApp.class);
    }

}
