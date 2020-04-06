package com.nbenja.weathertracker.service;

import com.nbenja.weathertracker.domain.Measurement;
import com.nbenja.weathertracker.domain.Statistic;
import com.nbenja.weathertracker.repository.MeasurementStore;
import com.nbenja.weathertracker.repository.MeasurementStoreInMemoryImpl;
import com.nbenja.weathertracker.domain.AggregateResult;
import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MeasurementAggregatorImplTest {

    private MeasurementAggregatorImpl subject;
    private MeasurementStore measurementStore;

    @Before
    public void before() {
        measurementStore = new MeasurementStoreInMemoryImpl();
        subject = new MeasurementAggregatorImpl(measurementStore);
    }

    @Test
    public void analyze_withMinAndMaxAndMetricsTemperature_returnStatistics() {
        List<String> metrics = asList("temperature");
        List<Statistic> statistics = asList(Statistic.MIN, Statistic.MAX);

        List<Measurement> measurements = new ArrayList<>();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-09-01T16:20:00.000Z");
        measurements.add(new Measurement(zonedDateTime, new Double(27.1), new Double(16.7), new Double(0)));
        measurements.add(new Measurement(zonedDateTime, new Double(28.1), new Double(16.7), new Double(0)));
        measurements.add(new Measurement(zonedDateTime, new Double(29.1), new Double(16.7), new Double(0)));

        List<AggregateResult> actual = subject.analyze(measurements, metrics, statistics);
        AggregateResult actualMin = actual.stream().filter(s -> s.getStatistic().equals(Statistic.MIN)).collect(Collectors.toList()).get(0);
        AggregateResult actualMax = actual.stream().filter(s -> s.getStatistic().equals(Statistic.MAX)).collect(Collectors.toList()).get(0);
        assertThat(actualMin.getValue(), is(equalTo(27.1)));
        assertThat(actualMax.getValue(), is(equalTo(29.1)));
    }


    @Test
    public void analyze_withMinAndMaxAndAllMetrics_returnStatistics() {
        List<String> metrics = asList("temperature", "dewPoint", "precipitation");
        List<Statistic> statistics = asList(Statistic.MIN, Statistic.MAX, Statistic.AVERAGE);

        List<Measurement> measurements = new ArrayList<>();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-09-01T16:20:00.000Z");
        measurements.add(new Measurement(zonedDateTime, new Double(27.1), new Double(14.7), null));
        measurements.add(new Measurement(zonedDateTime, new Double(28.1), new Double(16.7), null));
        measurements.add(new Measurement(zonedDateTime, new Double(29.1), new Double(18.7), null));

        List<AggregateResult> actual = subject.analyze(measurements, metrics, statistics);
        AggregateResult actualTempMin = actual.stream().filter(s -> (s.getStatistic().equals(Statistic.MIN) && s.getMetric().equalsIgnoreCase("temperature"))).collect(Collectors.toList()).get(0);
        AggregateResult actualTempMax = actual.stream().filter(s -> (s.getStatistic().equals(Statistic.MAX) && s.getMetric().equalsIgnoreCase("temperature"))).collect(Collectors.toList()).get(0);
        AggregateResult actualTempAvg = actual.stream().filter(s -> (s.getStatistic().equals(Statistic.AVERAGE) && s.getMetric().equalsIgnoreCase("temperature"))).collect(Collectors.toList()).get(0);
        DecimalFormat df = new DecimalFormat("#.#");
        assertThat(actualTempMin.getValue(), is(equalTo(27.1)));
        assertThat(actualTempMax.getValue(), is(equalTo(29.1)));
        assertThat(Double.parseDouble(df.format(actualTempAvg.getValue())), is(equalTo(28.1)));
    }
}