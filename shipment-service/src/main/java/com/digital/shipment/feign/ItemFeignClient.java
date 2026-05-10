package com.digital.shipment.feign;

import com.digital.shipment.feign.dto.ItemResponseDto;
import com.digital.shipment.feign.fallback.ItemFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item-service", fallback = ItemFeignClientFallback.class)
public interface ItemFeignClient {

    @GetMapping("/api/items/{id}")
    ItemResponseDto getItemById(@PathVariable Long id);
}
