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
public class DelayDetectedEvent {
    private Long shipmentId;
    private Long supplierId;
    private Long transporterId;
    private String fromLocation;
    private String toLocation;
    private LocalDateTime expectedDelivery;
    private LocalDateTime detectedAt;
}
