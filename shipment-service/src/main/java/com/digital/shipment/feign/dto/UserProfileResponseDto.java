package com.digital.shipment.feign.dto;

import lombok.Data;

/**
 * Local stub DTO mirroring user-service's UserProfileResponseDto.
 * Coordinate field names with user-service team before finalizing.
 */
@Data
public class UserProfileResponseDto {
    private Long userId;
    private String fullName;
    private String role;
    private Boolean isActive;
}
