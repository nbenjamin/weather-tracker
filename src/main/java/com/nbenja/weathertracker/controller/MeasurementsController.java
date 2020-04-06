package com.nbenja.weathertracker.controller;

import com.nbenja.weathertracker.domain.Measurement;
import com.nbenja.weathertracker.service.MeasurementServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/measurements")
public class MeasurementsController {

    private final static Logger LOGGER = LoggerFactory.getLogger(MeasurementsController.class);

    private MeasurementServiceImpl measurementService;
    private DateTimeFormatter dateTimeFormatter;

    public MeasurementsController(MeasurementServiceImpl measurementService, DateTimeFormatter dateTimeFormatter) {
        this.measurementService = measurementService;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    // features/01-measurements/01-add-measurement.feature
    @PostMapping
    public ResponseEntity<?> createMeasurement(@Valid @RequestBody Measurement measurement) {
        LOGGER.info("createMeasurement method - measurement {}", measurement);
        measurementService.add(measurement);
        return ResponseEntity
                .created(URI.create("/measurements/" + dateTimeFormatter.format(measurement.getTimestamp())))
                .build();
    }

    // features/01-measurements/02-get-measurement.feature
    @GetMapping("/{timestamp}")
    public ResponseEntity<Measurement> getMeasurement(@PathVariable ZonedDateTime timestamp) {
        LOGGER.info("getMeasurement method - timestamp {}", timestamp);
        Measurement measurement = measurementService.fetch(timestamp);

        if (measurement != null) {
            return ResponseEntity.ok(measurement);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
