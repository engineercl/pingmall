package com.pingmall.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
//表示当前类是一个属性读取类（prefix指定各个属性在yml文件中的前缀）
//本类属性的值通过读取yml文件，然后通过set方法注入。
@ConfigurationProperties(prefix = "pingmall.filter")
public class FilterProperties {
    private List<String> allowPaths;
}
