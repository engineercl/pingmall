package com.pingmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.pingmall.order.mapper")
public class PingMallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PingMallOrderApplication.class, args);
    }
}
