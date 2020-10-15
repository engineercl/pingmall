package com.pingmall.search.client;

import com.pingmall.item.api.GoodsAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface GoodsClient extends GoodsAPI {
}
