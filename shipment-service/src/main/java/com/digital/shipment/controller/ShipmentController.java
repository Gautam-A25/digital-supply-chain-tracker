package com.digital.shipment.controller;

import com.digital.shipment.dto.*;
import com.digital.shipment.entity.enums.ShipmentStatus;
import com.digital.shipment.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Shipments", description = "Manage supply chain shipments")
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    // ── POST /api/shipments — SUPPLIER
    @Operation(summary = "Create a new shipment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Shipment created"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "503", description = "item-service unavailable")
    })
    @PostMapping
    public ResponseEntity<ShipmentResponseDto> createShipment(
            @Valid @RequestBody CreateShipmentRequestDto dto,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shipmentService.createShipment(dto, userId));
    }

    // ── GET /api/shipments — ALL
    @Operation(summary = "List all shipments")
    @GetMapping
    public ResponseEntity<List<ShipmentResponseDto>> getAllShipments() {
        return ResponseEntity.ok(shipmentService.getAllShipments());
    }

    // ── GET /api/shipments/{id} — ALL
    @Operation(summary = "Get shipment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shipment found"),
            @ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDto> getShipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentById(id));
    }

    // ── PUT /api/shipments/{id}/assign — ADMIN
    @Operation(summary = "Assign a transporter to a shipment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transporter assigned"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "404", description = "Shipment or transporter not found")
    })
    @PutMapping("/{id}/assign")
    public ResponseEntity<ShipmentResponseDto> assignTransporter(
            @PathVariable Long id,
            @Valid @RequestBody AssignTransporterRequestDto dto) {
        return ResponseEntity.ok(shipmentService.assignTransporter(id, dto));
    }

    // ── PUT /api/shipments/{id}/status — TRANSPORTER
    @Operation(summary = "Update shipment status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<ShipmentResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequestDto dto,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(shipmentService.updateStatus(id, dto, userId));
    }

    // ── PUT /api/shipments/{id}/receive — WAREHOUSE_MANAGER
    @Operation(summary = "Confirm warehouse receipt — marks shipment as DELIVERED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shipment received"),
            @ApiResponse(responseCode = "400", description = "Already delivered or cancelled"),
            @ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    @PutMapping("/{id}/receive")
    public ResponseEntity<ShipmentResponseDto> receiveShipment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(shipmentService.receiveShipment(id, userId));
    }

    // ── GET /api/shipments/supplier/{id} — SUPPLIER / ADMIN
    @Operation(summary = "Get all shipments by supplier")
    @GetMapping("/supplier/{id}")
    public ResponseEntity<List<ShipmentResponseDto>> getBySupplier(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentsBySupplier(id));
    }

    // ── GET /api/shipments/transporter/{id} — TRANSPORTER / ADMIN
    @Operation(summary = "Get all shipments by transporter")
    @GetMapping("/transporter/{id}")
    public ResponseEntity<List<ShipmentResponseDto>> getByTransporter(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentsByTransporter(id));
    }

    // ── GET /api/shipments/status/{status} — ADMIN
    @Operation(summary = "Filter shipments by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShipmentResponseDto>> getByStatus(
            @PathVariable ShipmentStatus status) {
        return ResponseEntity.ok(shipmentService.getShipmentsByStatus(status));
    }

    // ── DELETE /api/shipments/{id} — SUPPLIER / ADMIN
    @Operation(summary = "Cancel a shipment — only allowed in CREATED state")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Shipment cancelled"),
            @ApiResponse(responseCode = "400", description = "Shipment not in CREATED state"),
            @ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelShipment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        shipmentService.cancelShipment(id, userId);
        return ResponseEntity.noContent().build();
    }
}
