package com.digital.shipment.messaging;

import com.digital.shipment.messaging.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishShipmentCreated(ShipmentCreatedEvent event) {
        log.info("Publishing ShipmentCreated event for shipmentId: {}", event.getShipmentId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SUPPLY_EXCHANGE,
                RabbitMQConfig.KEY_SHIPMENT_CREATED,
                event
        );
    }

    public void publishShipmentStatusChanged(ShipmentStatusChangedEvent event) {
        log.info("Publishing ShipmentStatusChanged event for shipmentId: {} | {} -> {}",
                event.getShipmentId(), event.getOldStatus(), event.getNewStatus());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SUPPLY_EXCHANGE,
                RabbitMQConfig.KEY_SHIPMENT_STATUS_CHANGED,
                event
        );
    }

    public void publishDelayDetected(DelayDetectedEvent event) {
        log.warn("Publishing DelayDetected event for shipmentId: {}", event.getShipmentId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SUPPLY_EXCHANGE,
                RabbitMQConfig.KEY_DELAY_DETECTED,
                event
        );
    }

    public void publishDamageReported(DamageReportedEvent event) {
        log.warn("Publishing DamageReported event for shipmentId: {}", event.getShipmentId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SUPPLY_EXCHANGE,
                RabbitMQConfig.KEY_DAMAGE_REPORTED,
                event
        );
    }
}
