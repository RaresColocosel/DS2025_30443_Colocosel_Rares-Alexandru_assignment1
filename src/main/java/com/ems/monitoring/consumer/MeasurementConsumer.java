package com.ems.monitoring.consumer;

import com.ems.monitoring.services.MonitoringService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MeasurementConsumer {

    private final MonitoringService monitoringService;
    private final ObjectMapper objectMapper;

    public MeasurementConsumer(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = "device_data_queue")
    public void receiveMeasurement(String messageJson) {
        try {
            JsonNode jsonNode = objectMapper.readTree(messageJson);
            Long timestamp = jsonNode.get("timestamp").asLong();
            Long deviceId = jsonNode.get("device_id").asLong();
            Double value = jsonNode.get("measurement_value").asDouble();

            monitoringService.processMeasurement(deviceId, value, timestamp);
        } catch (Exception e) {
            System.err.println("Error parsing measurement: " + e.getMessage());
        }
    }
}