package com.digital.shipment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignTransporterRequestDto {

    @NotNull(message = "Transporter ID is required")
    private Long transporterId;
}
