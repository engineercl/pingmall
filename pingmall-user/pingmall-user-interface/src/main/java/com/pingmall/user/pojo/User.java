package com.pingmall.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 6, max = 30, message = "用户名必须在6到30位")
    private String username;    // 用户名

    //对象序列化为JSON时忽略该属性
    @JsonIgnore
    @Length(min = 6, max = 30, message = "密码必须在6到30位")
    private String password;    // 密码

    @Pattern(regexp = "^1[356789]\\d{9}$",message = "请输入正确的手机号码")
    private String phone;       // 电话

    private Date created;       // 创建时间

    @JsonIgnore
    private String salt;        // 密码的盐值
}