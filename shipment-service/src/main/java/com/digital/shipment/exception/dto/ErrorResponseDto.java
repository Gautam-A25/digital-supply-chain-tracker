package com.digital.shipment.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private int status;
    private String message;
    private Map<String, String> errors;
    private LocalDateTime timestamp;
}
