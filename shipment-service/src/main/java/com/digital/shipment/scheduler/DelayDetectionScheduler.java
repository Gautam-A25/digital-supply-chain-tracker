package com.digital.shipment.scheduler;

import com.digital.shipment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DelayDetectionScheduler {

    private final ShipmentService shipmentService;

    /**
     * Runs every 15 minutes.
     * Finds shipments past their expectedDelivery that are not yet DELIVERED or CANCELLED,
     * marks them DELAYED, and publishes a DelayDetected event to RabbitMQ.
     */
    @Scheduled(cron = "0 */15 * * * *")
    public void detectDelayedShipments() {
        log.info("Running delay detection cron job...");
        shipmentService.processOverdueShipments();
    }
}
