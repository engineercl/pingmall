package com.pingmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.pingmall.item.mapper")
public class PingMallItemServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(PingMallItemServiceApp.class);
    }

}
