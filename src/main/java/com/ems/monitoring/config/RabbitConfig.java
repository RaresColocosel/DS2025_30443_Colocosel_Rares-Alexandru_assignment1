package com.ems.monitoring.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // --- EXISTING SENSOR DATA CONFIG ---
    public static final String QUEUE_NAME = "device_data_queue";
    public static final String EXCHANGE_NAME = "device_data_exchange";
    public static final String ROUTING_KEY = "device.measurement";

    @Bean
    public Queue deviceDataQueue() { return new Queue(QUEUE_NAME, true); }
    @Bean
    public TopicExchange deviceDataExchange() { return new TopicExchange(EXCHANGE_NAME); }
    @Bean
    public Binding binding(Queue deviceDataQueue, TopicExchange deviceDataExchange) {
        return BindingBuilder.bind(deviceDataQueue).to(deviceDataExchange).with(ROUTING_KEY);
    }

    // ========================================================================
    // --- NEW: SYNC CONFIGURATION ---
    // ========================================================================

    // 1. User Synchronization
    public static final String USER_SYNC_QUEUE = "user_sync_queue";
    public static final String USER_SYNC_EXCHANGE = "user_sync_exchange";
    public static final String USER_ROUTING_KEY = "user.#"; // Matches user.create, user.delete

    @Bean
    public Queue userSyncQueue() { return new Queue(USER_SYNC_QUEUE, true); }

    @Bean
    public TopicExchange userSyncExchange() { return new TopicExchange(USER_SYNC_EXCHANGE); }

    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(userSyncQueue()).to(userSyncExchange()).with(USER_ROUTING_KEY);
    }

    // 2. Device Synchronization
    public static final String DEVICE_SYNC_QUEUE = "device_sync_queue";
    public static final String DEVICE_SYNC_EXCHANGE = "device_sync_exchange";
    public static final String DEVICE_ROUTING_KEY = "device.#";

    @Bean
    public Queue deviceSyncQueue() { return new Queue(DEVICE_SYNC_QUEUE, true); }

    @Bean
    public TopicExchange deviceSyncExchange() { return new TopicExchange(DEVICE_SYNC_EXCHANGE); }

    @Bean
    public Binding deviceBinding() {
        return BindingBuilder.bind(deviceSyncQueue()).to(deviceSyncExchange()).with(DEVICE_ROUTING_KEY);
    }
}