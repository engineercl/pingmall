package com.pingmall.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云短信服务属性读取类
 */
@ConfigurationProperties(prefix = "pingmall.sms")
@Data
public class SMSProperties {
    //阿里云AccessKeyID
    private String accessKeyId;
    //阿里云AccessKeySecret
    private String accessKeySecret;
    //签名名称
    private String signName;
    //模板名称
    private String verifyCodeTemplate;
}
