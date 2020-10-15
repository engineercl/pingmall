package com.pingmall.order.service.api;

import com.pingmall.item.api.GoodsAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "leyou-gateway", path = "/api/item")
public interface GoodsService extends GoodsAPI {
}
