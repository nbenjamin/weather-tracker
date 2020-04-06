package com.nbenja.weathertracker.service;

import java.util.List;

import com.nbenja.weathertracker.domain.Measurement;
import com.nbenja.weathertracker.domain.Statistic;
import com.nbenja.weathertracker.domain.AggregateResult;

public interface MeasurementAggregator {
  List<AggregateResult> analyze(List<Measurement> measurements, List<String> metrics, List<Statistic> stats);
}
