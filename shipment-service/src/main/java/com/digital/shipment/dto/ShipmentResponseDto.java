package com.digital.shipment.dto;

import com.digital.shipment.entity.enums.ShipmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipmentResponseDto {

    private Long id;
    private Long itemId;
    private String itemName;
    private Long supplierId;
    private String supplierName;
    private Long transporterId;
    private String transporterName;
    private String fromLocation;
    private String toLocation;
    private LocalDateTime expectedDelivery;
    private LocalDateTime actualDelivery;
    private ShipmentStatus currentStatus;
    private String notes;
    private LocalDateTime createdAt;
}
