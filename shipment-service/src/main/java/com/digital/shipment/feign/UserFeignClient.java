package com.digital.shipment.feign;

import com.digital.shipment.feign.dto.UserProfileResponseDto;
import com.digital.shipment.feign.fallback.UserFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {

    @GetMapping("/api/users/{id}")
    UserProfileResponseDto getUserById(@PathVariable Long id);
}
