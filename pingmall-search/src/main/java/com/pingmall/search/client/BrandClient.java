package com.pingmall.search.client;

import com.pingmall.item.api.BrandAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface BrandClient extends BrandAPI {
}
