package com.digital.shipment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateShipmentRequestDto {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotBlank(message = "From location is required")
    private String fromLocation;

    @NotBlank(message = "To location is required")
    private String toLocation;

    @NotNull(message = "Expected delivery date is required")
    @Future(message = "Expected delivery must be a future date")
    private LocalDateTime expectedDelivery;

    private String notes;
}
