package com.digital.shipment.feign.fallback;

import com.digital.shipment.exception.ServiceUnavailableException;
import com.digital.shipment.feign.UserFeignClient;
import com.digital.shipment.feign.dto.UserProfileResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public UserProfileResponseDto getUserById(Long id) {
        throw new ServiceUnavailableException("user-service is unavailable. Cannot validate user with id: " + id);
    }
}
