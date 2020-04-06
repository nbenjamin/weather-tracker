package com.nbenja.weathertracker.service;

import com.nbenja.weathertracker.domain.Measurement;
import com.nbenja.weathertracker.repository.MeasurementStore;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class MeasurementServiceImpl implements MeasurementService {

    private MeasurementStore measurementStore;

    public MeasurementServiceImpl(MeasurementStore measurementStore) {
        this.measurementStore = measurementStore;
    }

    @Override
    public void add(Measurement measurement) {
        this.measurementStore.add(measurement);
    }

    @Override
    public Measurement fetch(ZonedDateTime timestamp) {
        return this.measurementStore.fetch(timestamp);
    }

    @Override
    public List<Measurement> queryDateRange(ZonedDateTime from, ZonedDateTime to) {
        return this.measurementStore.queryDateRange(from, to);
    }
}
