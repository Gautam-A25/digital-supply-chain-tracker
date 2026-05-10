package com.digital.shipment.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCreatedEvent {
    private Long shipmentId;
    private Long itemId;
    private String itemName;
    private Long supplierId;
    private String supplierName;
    private String fromLocation;
    private String toLocation;
    private LocalDateTime expectedDelivery;
    private LocalDateTime createdAt;
}
