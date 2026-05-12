package com.digital.shipment.dto;

import com.digital.shipment.entity.enums.ShipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequestDto {

    @NotNull(message = "Status is required")
    private ShipmentStatus status;

    private String notes;

    private String location;
}
