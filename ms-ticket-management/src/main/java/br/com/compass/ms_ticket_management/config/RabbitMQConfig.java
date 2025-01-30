package br.com.compass.ms_ticket_management.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private final String queueName = "ticket-queue";

    @Bean
    public Queue ticketQueue() {
        return new Queue(queueName, true);
    }
}
