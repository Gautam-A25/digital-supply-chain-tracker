package com.digital.shipment.feign.fallback;

import com.digital.shipment.exception.ServiceUnavailableException;
import com.digital.shipment.feign.ItemFeignClient;
import com.digital.shipment.feign.dto.ItemResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ItemFeignClientFallback implements ItemFeignClient {

    @Override
    public ItemResponseDto getItemById(Long id) {
        throw new ServiceUnavailableException("item-service is unavailable. Cannot validate item with id: " + id);
    }
}
