package com.nbenja.weathertracker.service;

import com.nbenja.weathertracker.domain.Measurement;
import com.nbenja.weathertracker.domain.Statistic;
import com.nbenja.weathertracker.repository.MeasurementStore;
import com.nbenja.weathertracker.domain.AggregateResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeasurementAggregatorImpl implements MeasurementAggregator {

    private MeasurementStore measurementStore;

    public MeasurementAggregatorImpl(MeasurementStore measurementStore) {
        this.measurementStore = measurementStore;
    }

    @Override
    public List<AggregateResult> analyze(List<Measurement> measurements, List<String> metrics, List<Statistic> stats) {

        List<AggregateResult> aggregateResults = new ArrayList<>();

        metrics.stream().forEach(s -> {
            if (s.equalsIgnoreCase("temperature")) {
                if (stats.contains(Statistic.MIN)) {
                    aggregateResults.add(new AggregateResult("temperature", Statistic.MIN, measurements.stream().filter(m -> m.getTemperature()!= null).mapToDouble(m -> m.getTemperature()).min().orElse(0)));
                }
                if (stats.contains(Statistic.MAX)) {
                    aggregateResults.add(new AggregateResult("temperature", Statistic.MAX, measurements.stream().filter(m -> m.getTemperature()!= null).mapToDouble(m -> m.getTemperature()).max().orElse(0)));
                }
                if (stats.contains(Statistic.AVERAGE)) {
                    aggregateResults.add(new AggregateResult("temperature", Statistic.AVERAGE, measurements.stream().filter(m -> m.getTemperature()!= null).mapToDouble(m -> m.getTemperature()).average().orElse(0)));
                }
            }
            if (s.equalsIgnoreCase("dewPoint")) {
                if (stats.contains(Statistic.MIN)) {
                    aggregateResults.add(new AggregateResult("dewPoint", Statistic.MIN, measurements.stream().filter(m -> m.getDewPoint()!= null).mapToDouble(m -> m.getDewPoint()).min().orElse(0)));
                }
                if (stats.contains(Statistic.MAX)) {
                    aggregateResults.add(new AggregateResult("dewPoint", Statistic.MAX, measurements.stream().filter(m -> m.getDewPoint()!= null).mapToDouble(m -> m.getDewPoint()).max().orElse(0)));
                }
                if (stats.contains(Statistic.AVERAGE)) {
                    aggregateResults.add(new AggregateResult("dewPoint", Statistic.AVERAGE, measurements.stream().filter(m -> m.getDewPoint()!= null).mapToDouble(m -> m.getDewPoint()).average().orElse(0)));
                }
            }
            if (s.equalsIgnoreCase("precipitation")) {
                if (stats.contains(Statistic.MIN)) {
                    aggregateResults.add(new AggregateResult("precipitation", Statistic.MIN, measurements.stream().filter(m -> m.getPrecipitation() != null).mapToDouble(m -> m.getPrecipitation()).min().orElse(0)));
                }
                if (stats.contains(Statistic.MAX)) {
                    aggregateResults.add(new AggregateResult("precipitation", Statistic.MAX, measurements.stream().filter(m -> m.getPrecipitation() != null).mapToDouble(m -> m.getPrecipitation()).max().orElse(0)));
                }
                if (stats.contains(Statistic.AVERAGE)) {
                    aggregateResults.add(new AggregateResult("precipitation", Statistic.AVERAGE, measurements.stream().filter(m -> m.getPrecipitation() != null).mapToDouble(m -> m.getPrecipitation()).average().orElse(0)));
                }
            }
        });
        return aggregateResults.stream().filter(f-> f.getValue()>0).collect(Collectors.toList());
    }
}
