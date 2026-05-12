package com.digital.shipment.entity;

import com.digital.shipment.entity.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Item info (validated via Feign on creation, name denormalized)
    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private String itemName;

    // Supplier info (from JWT header on creation)
    @Column(nullable = false)
    private Long supplierId;

    private String supplierName;

    // Transporter info (set when ADMIN assigns)
    private Long transporterId;

    private String transporterName;

    // Locations
    @Column(nullable = false)
    private String fromLocation;

    @Column(nullable = false)
    private String toLocation;

    // Delivery times
    @Column(nullable = false)
    private LocalDateTime expectedDelivery;

    private LocalDateTime actualDelivery;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus currentStatus;

    @Column(length = 1000)
    private String notes;

    // Audit
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
