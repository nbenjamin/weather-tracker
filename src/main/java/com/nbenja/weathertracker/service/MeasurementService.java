package com.nbenja.weathertracker.service;

import com.nbenja.weathertracker.domain.Measurement;

import java.util.List;
import java.time.ZonedDateTime;

public interface MeasurementService {

  void add(Measurement measurement);

  Measurement fetch(ZonedDateTime timestamp);

  List<Measurement> queryDateRange(ZonedDateTime from, ZonedDateTime to);
}
