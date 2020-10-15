package com.pingmall.search.client;

import com.pingmall.item.api.SpecificationAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface SpecificationClient extends SpecificationAPI {
}
