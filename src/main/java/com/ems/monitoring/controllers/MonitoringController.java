package com.ems.monitoring.controllers;

import com.ems.monitoring.entities.HourlyConsumption;
import com.ems.monitoring.services.MonitoringService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/monitoring")
@CrossOrigin // Allows frontend requests
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    /**
     * GET /monitoring/consumption
     * Optional Params:
     * - deviceId (Long): Filter by specific device
     * - date (YYYY-MM-DD): Filter by specific day
     * * Returns: List of database entries (listings).
     */
    @GetMapping("/consumption")
    public ResponseEntity<List<HourlyConsumption>> getConsumption(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<HourlyConsumption> listings = monitoringService.getConsumption(deviceId, date);
        return ResponseEntity.ok(listings);
    }
}