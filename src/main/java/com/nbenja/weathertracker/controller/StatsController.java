package com.nbenja.weathertracker.controller;

import java.beans.PropertyEditorSupport;
import java.time.ZonedDateTime;
import java.util.List;

import com.nbenja.weathertracker.domain.Measurement;
import com.nbenja.weathertracker.domain.Statistic;

import com.nbenja.weathertracker.service.MeasurementServiceImpl;
import com.nbenja.weathertracker.domain.AggregateResult;
import com.nbenja.weathertracker.service.MeasurementAggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final static Logger LOGGER = LoggerFactory.getLogger(StatsController.class);

    private MeasurementServiceImpl queryService;
    private MeasurementAggregator aggregator;

    public StatsController(MeasurementServiceImpl queryService, MeasurementAggregator aggregator) {
        this.queryService = queryService;
        this.aggregator = aggregator;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(Statistic.class, new StatisticsConverter());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AggregateResult> getStats(
            @RequestParam("metric") List<String> metrics,
            @RequestParam("stat") List<Statistic> stats,
            @RequestParam("fromDateTime") ZonedDateTime from,
            @RequestParam("toDateTime") ZonedDateTime to
    ) {
        LOGGER.info("getStats method - metrics {}, stats {}, from {}, to  {}", metrics, stats, from, to);
        List<Measurement> measurements = queryService.queryDateRange(from, to);
        return aggregator.analyze(measurements, metrics, stats);
    }

    public static class StatisticsConverter extends PropertyEditorSupport {
        @Override
        public void setAsText(String stats) throws IllegalArgumentException {
            switch (stats.toUpperCase()) {
                case "MIN":
                    setValue(Statistic.MIN);
                    break;
                case "MAX":
                    setValue(Statistic.MAX);
                    break;
                case "AVERAGE":
                    setValue(Statistic.AVERAGE);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Statistics type " + stats);
            }
        }
    }
}
