package com.pingmall.search.client;

import com.pingmall.item.api.CategoryAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface CategoryClient extends CategoryAPI {
}
