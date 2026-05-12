package com.digital.shipment.feign.dto;

import lombok.Data;

/**
 * Local stub DTO mirroring item-service's ItemResponseDto.
 * Coordinate field names with item-service team before finalizing.
 */
@Data
public class ItemResponseDto {
    private Long id;
    private String name;
    private Boolean isActive;
}
