package com.nbenja.weathertracker.repository;

import com.nbenja.weathertracker.domain.Measurement;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
public class MeasurementStoreInMemoryImpl implements MeasurementStore{

    private Map<ZonedDateTime, Measurement> measurementMap = new TreeMap<>();

    @Override
    public void add(Measurement measurement) {
        if(measurement.getTimestamp() != null) {
            measurementMap.put(measurement.getTimestamp(), measurement);
        }
    }

    @Override
    public Measurement fetch(ZonedDateTime timestamp) {
        return measurementMap.getOrDefault(timestamp, null);
    }

    @Override
    public List<Measurement> queryDateRange(ZonedDateTime from, ZonedDateTime to) {
        return measurementMap.entrySet().stream().filter( kv -> ((kv.getKey().isEqual(from) || kv.getKey().isAfter(from))
                && (kv.getKey().isBefore(to)))).map(v -> v.getValue()).collect(Collectors.toList());
    }
}
