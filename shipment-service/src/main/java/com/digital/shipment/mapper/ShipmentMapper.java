package com.digital.shipment.mapper;

import com.digital.shipment.dto.ShipmentResponseDto;
import com.digital.shipment.entity.Shipment;
import org.springframework.stereotype.Component;

@Component
public class ShipmentMapper {

    public ShipmentResponseDto toDto(Shipment shipment) {
        if (shipment == null) return null;

        ShipmentResponseDto dto = new ShipmentResponseDto();
        dto.setId(shipment.getId());
        dto.setItemId(shipment.getItemId());
        dto.setItemName(shipment.getItemName());
        dto.setSupplierId(shipment.getSupplierId());
        dto.setSupplierName(shipment.getSupplierName());
        dto.setTransporterId(shipment.getTransporterId());
        dto.setTransporterName(shipment.getTransporterName());
        dto.setFromLocation(shipment.getFromLocation());
        dto.setToLocation(shipment.getToLocation());
        dto.setExpectedDelivery(shipment.getExpectedDelivery());
        dto.setActualDelivery(shipment.getActualDelivery());
        dto.setCurrentStatus(shipment.getCurrentStatus());
        dto.setNotes(shipment.getNotes());
        dto.setCreatedAt(shipment.getCreatedAt());
        return dto;
    }
}
