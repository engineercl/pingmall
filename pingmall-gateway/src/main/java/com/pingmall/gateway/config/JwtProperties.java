package com.pingmall.gateway.config;

import com.pingmall.auth.utils.RsaUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
//表示当前类是一个属性读取类（prefix指定各个属性在yml文件中的前缀）
//本类属性的值通过读取yml文件，然后通过set方法注入。
@ConfigurationProperties(prefix = "pingmall.jwt")
public class JwtProperties {
    private String pubKeyPath;      // 公钥

    private PublicKey publicKey;    // 公钥

    private String cookieName;

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    /**
     * @PostContruct：在构造方法执行之后执行该方法
     */
    @PostConstruct
    public void init() {
        try {
            // 获取公钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException();
        }
    }
}
