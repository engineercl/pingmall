package com.pingmall.auth.client;

import com.pingmall.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

//表示当前接口是一个Feign客户端（必须指定调用服务名称）
@FeignClient("user-service")
public interface UserClient extends UserApi {
}
