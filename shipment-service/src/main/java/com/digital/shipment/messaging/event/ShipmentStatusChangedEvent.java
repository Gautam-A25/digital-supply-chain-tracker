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
public class ShipmentStatusChangedEvent {
    private Long shipmentId;
    private String oldStatus;
    private String newStatus;
    private Long updatedByUserId;
    private String notes;
    private LocalDateTime updatedAt;
}
