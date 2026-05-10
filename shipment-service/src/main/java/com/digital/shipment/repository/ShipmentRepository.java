package com.digital.shipment.repository;

import com.digital.shipment.entity.Shipment;
import com.digital.shipment.entity.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findBySupplierId(Long supplierId);

    List<Shipment> findByTransporterId(Long transporterId);

    List<Shipment> findByCurrentStatus(ShipmentStatus status);

    // Overdue: expectedDelivery passed, not yet delivered or cancelled
    @Query("SELECT s FROM Shipment s WHERE s.expectedDelivery < :now " +
           "AND s.currentStatus NOT IN ('DELIVERED', 'CANCELLED', 'DELAYED')")
    List<Shipment> findOverdueShipments(@Param("now") LocalDateTime now);
}
