package com.digital.shipment.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String SUPPLY_EXCHANGE    = "supply.exchange";
    public static final String DEAD_LETTER_EXCHANGE = "supply.dlx";

    // Routing keys (shipment-service publishes these)
    public static final String KEY_SHIPMENT_CREATED        = "shipment.created";
    public static final String KEY_SHIPMENT_STATUS_CHANGED = "shipment.status.changed";
    public static final String KEY_DELAY_DETECTED          = "shipment.delay.detected";
    public static final String KEY_DAMAGE_REPORTED         = "shipment.damage.reported";

    /** Main topic exchange — all shipment events are published here */
    @Bean
    public TopicExchange supplyExchange() {
        return new TopicExchange(SUPPLY_EXCHANGE);
    }

    /** Dead letter exchange for failed messages */
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DEAD_LETTER_EXCHANGE);
    }

    /** Use Spring's ObjectMapper so LocalDateTime serializes to standard ISO strings instead of arrays */
    @Bean
    public Jackson2JsonMessageConverter messageConverter(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /** Configure RabbitTemplate to use JSON converter */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
