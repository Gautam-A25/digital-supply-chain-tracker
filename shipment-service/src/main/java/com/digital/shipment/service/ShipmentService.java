package com.digital.shipment.service;

import com.digital.shipment.dto.*;
import com.digital.shipment.entity.Shipment;
import com.digital.shipment.entity.enums.ShipmentStatus;
import com.digital.shipment.exception.ResourceNotFoundException;
import com.digital.shipment.exception.ShipmentStateException;
import com.digital.shipment.feign.ItemFeignClient;
import com.digital.shipment.feign.UserFeignClient;
import com.digital.shipment.feign.dto.ItemResponseDto;
import com.digital.shipment.feign.dto.UserProfileResponseDto;
import com.digital.shipment.mapper.ShipmentMapper;
import com.digital.shipment.messaging.ShipmentEventPublisher;
import com.digital.shipment.messaging.event.*;
import com.digital.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper shipmentMapper;
    private final ItemFeignClient itemFeignClient;
    private final UserFeignClient userFeignClient;
    private final ShipmentEventPublisher eventPublisher;

    // ─────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────

    @Transactional
    public ShipmentResponseDto createShipment(CreateShipmentRequestDto dto, Long supplierId) {
        log.info("Creating shipment for supplierId: {}, itemId: {}", supplierId, dto.getItemId());

        // Validate item exists via Feign
        ItemResponseDto item = itemFeignClient.getItemById(dto.getItemId());
        if (Boolean.FALSE.equals(item.getIsActive())) {
            throw new ShipmentStateException("Item with id " + dto.getItemId() + " is not active");
        }

        // Get supplier name via Feign
        UserProfileResponseDto supplier = userFeignClient.getUserById(supplierId);

        Shipment shipment = Shipment.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .supplierId(supplierId)
                .supplierName(supplier.getFullName())
                .fromLocation(dto.getFromLocation())
                .toLocation(dto.getToLocation())
                .expectedDelivery(dto.getExpectedDelivery())
                .notes(dto.getNotes())
                .currentStatus(ShipmentStatus.CREATED)
                .build();

        Shipment saved = shipmentRepository.save(shipment);

        eventPublisher.publishShipmentCreated(ShipmentCreatedEvent.builder()
                .shipmentId(saved.getId())
                .itemId(saved.getItemId())
                .itemName(saved.getItemName())
                .supplierId(saved.getSupplierId())
                .supplierName(saved.getSupplierName())
                .fromLocation(saved.getFromLocation())
                .toLocation(saved.getToLocation())
                .expectedDelivery(saved.getExpectedDelivery())
                .createdAt(saved.getCreatedAt())
                .build());

        log.info("Shipment created with id: {}", saved.getId());
        return shipmentMapper.toDto(saved);
    }

    // ─────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> getAllShipments() {
        return shipmentRepository.findAll()
                .stream()
                .map(shipmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShipmentResponseDto getShipmentById(Long id) {
        Shipment shipment = findOrThrow(id);
        return shipmentMapper.toDto(shipment);
    }

    @Cacheable(value = "shipmentStatus", key = "#shipmentId")
    public String getShipmentStatus(Long shipmentId) {
        log.debug("Cache miss — fetching status from DB for shipmentId: {}", shipmentId);
        return findOrThrow(shipmentId).getCurrentStatus().name();
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> getShipmentsBySupplier(Long supplierId) {
        return shipmentRepository.findBySupplierId(supplierId)
                .stream().map(shipmentMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> getShipmentsByTransporter(Long transporterId) {
        return shipmentRepository.findByTransporterId(transporterId)
                .stream().map(shipmentMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findByCurrentStatus(status)
                .stream().map(shipmentMapper::toDto).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // UPDATE — Assign Transporter
    // ─────────────────────────────────────────────

    @Transactional
    public ShipmentResponseDto assignTransporter(Long shipmentId, AssignTransporterRequestDto dto) {
        Shipment shipment = findOrThrow(shipmentId);

        if (shipment.getCurrentStatus() != ShipmentStatus.CREATED) {
            throw new ShipmentStateException(
                    "Transporter can only be assigned when shipment is in CREATED state. Current: "
                    + shipment.getCurrentStatus());
        }

        // Validate transporter via Feign
        UserProfileResponseDto transporter = userFeignClient.getUserById(dto.getTransporterId());
        if (!"TRANSPORTER".equalsIgnoreCase(transporter.getRole())) {
            throw new ShipmentStateException("User " + dto.getTransporterId() + " is not a TRANSPORTER");
        }

        String oldStatus = shipment.getCurrentStatus().name();
        shipment.setTransporterId(transporter.getUserId());
        shipment.setTransporterName(transporter.getFullName());
        shipment.setCurrentStatus(ShipmentStatus.ASSIGNED);

        Shipment saved = shipmentRepository.save(shipment);
        updateStatusCache(shipmentId, ShipmentStatus.ASSIGNED.name());

        eventPublisher.publishShipmentStatusChanged(ShipmentStatusChangedEvent.builder()
                .shipmentId(shipmentId)
                .oldStatus(oldStatus)
                .newStatus(ShipmentStatus.ASSIGNED.name())
                .updatedAt(LocalDateTime.now())
                .build());

        return shipmentMapper.toDto(saved);
    }

    // ─────────────────────────────────────────────
    // UPDATE — Status (by TRANSPORTER)
    // ─────────────────────────────────────────────

    @Transactional
    public ShipmentResponseDto updateStatus(Long shipmentId, UpdateStatusRequestDto dto, Long userId) {
        Shipment shipment = findOrThrow(shipmentId);

        validateStatusTransition(shipment.getCurrentStatus(), dto.getStatus());

        String oldStatus = shipment.getCurrentStatus().name();
        shipment.setCurrentStatus(dto.getStatus());
        if (dto.getNotes() != null) {
            shipment.setNotes(dto.getNotes());
        }

        Shipment saved = shipmentRepository.save(shipment);
        updateStatusCache(shipmentId, dto.getStatus().name());

        // Publish StatusChanged for all updates
        eventPublisher.publishShipmentStatusChanged(ShipmentStatusChangedEvent.builder()
                .shipmentId(shipmentId)
                .oldStatus(oldStatus)
                .newStatus(dto.getStatus().name())
                .updatedByUserId(userId)
                .notes(dto.getNotes())
                .updatedAt(LocalDateTime.now())
                .build());

        // If DAMAGED — also publish DamageReported
        if (dto.getStatus() == ShipmentStatus.DAMAGED) {
            eventPublisher.publishDamageReported(DamageReportedEvent.builder()
                    .shipmentId(shipmentId)
                    .supplierId(shipment.getSupplierId())
                    .transporterId(shipment.getTransporterId())
                    .notes(dto.getNotes())
                    .reportedAt(LocalDateTime.now())
                    .build());
        }

        return shipmentMapper.toDto(saved);
    }

    // ─────────────────────────────────────────────
    // UPDATE — Confirm Receipt (WAREHOUSE_MANAGER)
    // ─────────────────────────────────────────────

    @Transactional
    public ShipmentResponseDto receiveShipment(Long shipmentId, Long userId) {
        Shipment shipment = findOrThrow(shipmentId);

        if (shipment.getCurrentStatus() == ShipmentStatus.DELIVERED) {
            throw new ShipmentStateException("Shipment " + shipmentId + " is already DELIVERED");
        }
        if (shipment.getCurrentStatus() == ShipmentStatus.CANCELLED) {
            throw new ShipmentStateException("Cannot receive a CANCELLED shipment");
        }

        String oldStatus = shipment.getCurrentStatus().name();
        shipment.setCurrentStatus(ShipmentStatus.DELIVERED);
        shipment.setActualDelivery(LocalDateTime.now());

        Shipment saved = shipmentRepository.save(shipment);
        updateStatusCache(shipmentId, ShipmentStatus.DELIVERED.name());

        eventPublisher.publishShipmentStatusChanged(ShipmentStatusChangedEvent.builder()
                .shipmentId(shipmentId)
                .oldStatus(oldStatus)
                .newStatus(ShipmentStatus.DELIVERED.name())
                .updatedByUserId(userId)
                .updatedAt(LocalDateTime.now())
                .build());

        return shipmentMapper.toDto(saved);
    }

    // ─────────────────────────────────────────────
    // DELETE — Cancel (CREATED state only)
    // ─────────────────────────────────────────────

    @Transactional
    public void cancelShipment(Long shipmentId, Long userId) {
        Shipment shipment = findOrThrow(shipmentId);

        if (shipment.getCurrentStatus() != ShipmentStatus.CREATED) {
            throw new ShipmentStateException(
                    "Shipment can only be cancelled in CREATED state. Current: "
                    + shipment.getCurrentStatus());
        }

        shipment.setCurrentStatus(ShipmentStatus.CANCELLED);
        shipmentRepository.save(shipment);
        updateStatusCache(shipmentId, ShipmentStatus.CANCELLED.name());
        log.info("Shipment {} cancelled by userId: {}", shipmentId, userId);
    }

    // ─────────────────────────────────────────────
    // SCHEDULER — called by DelayDetectionScheduler
    // ─────────────────────────────────────────────

    @Transactional
    public void processOverdueShipments() {
        List<Shipment> overdue = shipmentRepository.findOverdueShipments(LocalDateTime.now());
        log.info("Delay detection: found {} overdue shipments", overdue.size());

        for (Shipment shipment : overdue) {
            shipment.setCurrentStatus(ShipmentStatus.DELAYED);
            shipmentRepository.save(shipment);
            updateStatusCache(shipment.getId(), ShipmentStatus.DELAYED.name());

            eventPublisher.publishDelayDetected(DelayDetectedEvent.builder()
                    .shipmentId(shipment.getId())
                    .supplierId(shipment.getSupplierId())
                    .transporterId(shipment.getTransporterId())
                    .fromLocation(shipment.getFromLocation())
                    .toLocation(shipment.getToLocation())
                    .expectedDelivery(shipment.getExpectedDelivery())
                    .detectedAt(LocalDateTime.now())
                    .build());
        }
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────

    private Shipment findOrThrow(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
    }

    @CachePut(value = "shipmentStatus", key = "#shipmentId")
    public String updateStatusCache(Long shipmentId, String status) {
        return status;
    }

    private void validateStatusTransition(ShipmentStatus current, ShipmentStatus next) {
        // CANCELLED and DELIVERED are terminal states
        if (current == ShipmentStatus.CANCELLED) {
            throw new ShipmentStateException("Cannot update a CANCELLED shipment");
        }
        if (current == ShipmentStatus.DELIVERED) {
            throw new ShipmentStateException("Cannot update a DELIVERED shipment");
        }
        // Must be ASSIGNED before going IN_TRANSIT
        if (next == ShipmentStatus.IN_TRANSIT && current == ShipmentStatus.CREATED) {
            throw new ShipmentStateException("Shipment must be ASSIGNED before moving to IN_TRANSIT");
        }
    }
}
