package com.digital.shipment.mapper;

import com.digital.shipment.dto.ShipmentResponseDto;
import com.digital.shipment.entity.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShipmentMapper {

    ShipmentResponseDto toDto(Shipment shipment);
}
